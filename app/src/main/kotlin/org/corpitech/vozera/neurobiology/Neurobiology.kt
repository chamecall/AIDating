package org.corpitech.vozera.neurobiology

class Neurobiology {
    var Dominance = 0
    var Sexual = 0
    var Resource = 0
    var Social = 0

    val Value: Int
        get() {
            return (Dominance + Sexual + Resource + Social) * 999 / 396 // 4*99
        }



}