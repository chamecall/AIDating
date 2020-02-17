package org.corpitech.vozera.neurobiology

class Neurobiology(_sex : Int, _age : Int, _beauty : Int) {
    var Dominance = 0
    var Sexual = 0
    var Resource = 0
    var Social = 0

    val Value: Int
        get() {
            return (Dominance + Sexual + Resource + Social) * 999 / 396 // 4*99
        }

    private val _sexHistory : MutableList<Int> = mutableListOf();
    private val _ageHistory : MutableList<Int> = mutableListOf();
    private val _beautyHistory : MutableList<Int> = mutableListOf();



    init {

    }



}