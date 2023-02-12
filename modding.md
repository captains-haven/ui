## Layouts

Example Furnace:

```
   [2][2][2][3][3][3][3][3][2]>~Y
   [2][2][3][5][5][7][7][4][3]>'O
A~>[2][2][3][5][5][7][7][4][3]>'V
B~>[2][2][3][5][5][7][7][4][3]>'W
   [2][2][2][3][3][7][7][4][3]>'P
   [2][2][2][2][2][2][2][2][3]>@E
```

`A~>` and `B~>` are In Ports, they accept products.

`>@E` is exhaust

`>~Y` is slag exit

`>'` are Out Ports, they emit products

The `>` indicates the direction of the port. They can be one of the following:

- `>`
- `<`
- `^`
- `v`

There is also a `+` port type, but meaning is unclear as of yet.

`[1]` - `[9]` (`[n]` essentially) are heigh units, they define the "hitbox" of the building, how many squares
it takes up.

Example Chemical Plant:

```
~E>[7][8][7][6][5][5][5]   
   [7][7][7][6][5][5][5]   
#D>[6][6][6][6][5][5][5]>X@
@A>[5][5][5][5][5][5][5]>Y#
@B>[5][5][5][5][5][5][5]   
@C>[5][5][5][5][5][5][5]   
   [5][5][5][5][5][5][5]   
```

Example Unit storage:

```
   [4][4][4][4][4]   
 # >4A[4][4][4]X4> # 
   [4][4][4][4][4]   
 # >4B[4][4][4]Y4> # 
   [4][4][4][4][4]   
```

(wrapped on the left and right with whitespace? Why?)

Example loose storage

```
"   [6][6][6][6][6]   ",
" ~ >6A[6][6][6]X6> ~ ",
"   [6][6][6][6][6]   ",
" ~ >6B[6][6][6]Y6> ~ ",
"   [6][6][6][6][6]   "
```

Example fluid storage:

```
"   [5][5][5][5][5]   ",
" @ >5A[5][5][5]X5> @ ",
"   [5][5][5][5][5]   ",
" @ >5B[5][5][5]Y5> @ ",
"   [5][5][5][5][5]   "
```

With this, we can establish that `#` is for transport belts, `~` is for loose storage belts and `@` is for pipes.
According to furnace, `'` is for melted materials.