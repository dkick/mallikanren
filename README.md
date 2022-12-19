# mallikanren

A Clojure library designed to combine Malli and core.logic; i.e. use
Malli transform to generate core.logic relations for the Malli spec. I
lost a git repo for this so I'm starting over from scratch, from
memory. Ugh.

## Usage

FIXME

## Implementation notes

The Malli source code uses the convention of a leading `?` for schema
names. For example ...

```
(defn -reference? [?schema]
  (or (string? ?schema) (qualified-keyword? ?schema)))
```

I am keeping with this convention; I kind of like it.

miniKanren (and core.logic) use a naming convention of a trailing
`o`. For example, consider the relation function
`clojure.core.logic.membero`. I am not keeping with this convention; I
do not like it. If I had a relation function to I wanted to name `go`,
this naming convention would turn it into `goo`. I am using `!?`
instead. The `!` implies a side-effecting, which is kind of what
unification does, though not really; it only establishes a
binding. The `?` imples a predicate, which is kind of what
`cloure.core.logic.run` does by answering whether or not all of the
constraints can be satisfied. And so `!?` is, in my mind, a bit like
what `run` does with logic variables; it answers a question by
unifying values, which is a bit like `set!`ting them, but not really.

## License

Copyright © 2022

This program and the accompanying materials are made available under
the terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following
Secondary Licenses when the conditions for such availability set forth
in the Eclipse Public License, v. 2.0 are satisfied: GNU General
Public License as published by the Free Software Foundation, either
version 2 of the License, or (at your option) any later version, with
the GNU Classpath Exception which is available at
https://www.gnu.org/software/classpath/license.html.
