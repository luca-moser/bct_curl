# Batched Binary Encoded Ternary Curl

This repository implements a batched binary encoded ternary Curl, using Hans Moog's Go implementation as a reference.

The batched BCTCurl collects up to a timeout of 50 milliseconds trits inputs to hash. All the inputs must be of the same length.

On an i7-8850u CPU, it is able to throughput ~15k txs hashes/s (**per core**) given 8019 trits of transaction input 
data per request. Running on 4 cores, this yields over **~50k transaction hashes per second**.

A basic Curl implementation achieves only a throughput of about 320 txs hashes/s per core.

### 1. Multiplexing
| The description below was written by studying the Go implementation written by Hans Moog

The multiplexer transforms inputs of trits slices onto a slice of N times the size of a single input of 64-bit uints.
Given for example input trits of 8019 (the trit size of a tx) and 64 requests (64 txs to hash), the multiplexer
allocates a uint64 slice of 8019 in length. `(64.152 kilobytes; ((8019 * 64) / 8) / 1000)`
For each trit at the location Y, the low and high bits are set accordingly to the formula of:
(trit value) `-1 => high 0, low 1; 0 => high 1, low 1; 1 => high 1, low 0`; using OR bitwise operations.
Thereby, the multiplexer can multiplex up to 64 requests as a uint64 holds 64 bits. 
(on a 32-bit machine it will batch up to 32 requests)

After multiplexing the input data onto slices of uints, a new BatchedCurl with the trit-size hash length
and the number of rounds to perform is instantiated. 
The state of the BatchedCurl consists of a low and high uint slice, each with a capacity of `3 * the request trit hash length`.
The total state length is thereby `3 * the requested trit hash length`. Additionally a passed in hash length is initialized.

### 2. Absorbing
The absorb function of BatchedCurl takes in a low and high uint slice tuple, then copies requested trits hash length size
into the state per iteration or the remaining amount of trits available from the input tuple if it's less than the 
requested trits hash length size. After each copying of the requested trits hash length/remaining trits, the transform
function is applied to the state. Thereby, given a tuple input of 8019 uints and requested trits hash length size of 243,
the transform function is called: `8019 / 243 = 33 times (i.e. 33 x number of rounds)`. The uints are always copied into first third of the state
before each transformation.

### 3. Transforming
Curl uses a substitution and permutation network for diffusion and confusion. 
The permutation is done by taking two indices from the permutation set: `[0 364 728 363 727 362 726 361 725 360 724 359 723 358 722 357 721 356 720 ... 10 374 9 373 8 372 7 371 6 370 5 369 4 368 3 367 2 366 1 365 0]` 
and using the corresponding trits from the state at the given location against the S-Box. 
Curl's S-Box or substitution box looks like this:
```
      y=-1, y=0, y=1
x=-1 [1,    0,   -1]
x=0  [1,   -1,    0]
x=1  [-1,   1,    0]
``` 
So given for example `x=1, y=-1`, the S-Box yields `-1`.

If we had a state size of 9 (therefore indices `0,1,2,3,4,5,6,7,8`), the permutation set would look like this:
`[0,4,8,3,7,2,6,1,5,0]` and the corresponding pairs: `[[0, 4],[4, 8],[8, 3],[3, 7],[7, 2],[2, 6],[6, 1],[1, 5],[5, 0]]` would be
used as indices to pull the trits from the state against the S-Box.
Thereby Curl's transformation function copies the state per round, then iterates over the state and pulls the permutation indices 
at `stateIndex` and `stateIndex + 1`, uses these permutation indices to pull the corresponding trits from the state and 
sets the state trit at the current state index accordingly to the derived S-Box value.
In short (pseudo code):
```
var stateCopy []int
for round = 0; round < numRounds; round++ {
	stateCopy = copy(stateCopy, state)
	for stateIndex = 0; stateIndex < stateSize; stateIndex++ {
		state[stateIndex] = S-Box(state[permutIndices[stateIndex]], state[permutIndices[stateIndex + 1]]).	
	}
}
```

The BatchedCurl transform function allocates two scratchpads uint slices of the state length (`3 * requested trit hash length`)
and a scratchpad index of 0.
Per round, the state's low and high uints are copied into the scratchpads and then per index of the state:
`alpha = low scratchpad[scratchpadIndex]` and `beta = high scratchpad[scratchpadIndex]` are initialized.
if the scratchpad index is less than 365, then it's incremented by 364. Otherwise it's decremented by 365.
delta is initialized as the XOR of beta (high uint) and low scratchpad[scratchpadIndex] => `delta = beta ^ low scratchpad[scratchpadIndex]`
delta is then used to apply the substitution/S-Box by setting: 
the state's low uint at the state index by XORing the AND bitwise addition of delta and alpha => `^(delta & alpha)` 
the state's high uint at the state index by ORing delta with the XOR of alpha and the uint at the 
high scratchpad[scratchpadIndex] => `delta | (alpha ^ high scratchpad[scratchpadIndex])`

### 4. Squeezing
The squeeze function allocates a new low and high uint slice of the requested squeeze size (which should be a multiple of the state size).
Then a hashCount is initialized as the number of hash sizes to extract from the state: `hashCount = requested squeeze size / hash length`.
Given for example a requested squeeze size of 729 and a hash length of 243, the hashCount would be initialized with 3.
For each hashCount, the hash length uints are copied from the state into the result uint slice into the appropriate location. 
After each copy, the transformation function is applied to the state.

### 5. Demultiplexing
After squeezing out the low and high uint slices representing the low/high trits of the hash per input request, they have to
be demultiplexed back into the corresponding hash. For demultiplexing a new result trits slice is allocated with the specified length
of the hash size during squeezing. Then the low and high uint slices are iterated over and the corresponding (partaining to the origin input request) low and high value is taken by bitshifting the uint value into the least signficant bit (first bit from the right) and ANDed it with 1 to get zero or one => low = (squeezeResultLow[i] >> index) & 1, high = (squeezeResultHigh[i] >> index) & 1. The low and high values
are then taken to compute the final trit under the formula `-1 => high 0, low 1; 0 => high 1, low 1; 1 => high 1, low 0`; which gets
put into the result slice. Each result slice is communicated back to the initial caller which supplied the input request.