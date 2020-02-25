package org.corpitech.vozera.neurobiology

class Neurobiology(_sex : Int, _age : Int, _beauty : Double) {
    var Dominance = 50
    var Sexual = 50
    var Resource = 50
    var Social = 50

    val Value: Int
        get() {
            return (Dominance + Sexual + Resource + Social) * 999 / 396 // 4*99
        }

    private val _sexHistory : MutableList<Int> = mutableListOf();
    private val _ageHistory : MutableList<Int> = mutableListOf();
    private val _beautyHistory : MutableList<Double> = mutableListOf();

    init {

    }

    fun UpdateScores(_sex: Int, _age: Int, _beauty: Double) {
        if (_sexHistory.count() >= 4) {
            _sexHistory.removeAt(0)
        }
        _sexHistory.add(_sex)
        if (_ageHistory.count() >= 4) {
            _ageHistory.removeAt(0)
        }
        _ageHistory.add(_age)
        if (_beautyHistory.count() >= 4) {
            _beautyHistory.removeAt(0)
        }
        _beautyHistory.add(_beauty)

        val beautyForCalculation = _sexHistory.average()
        val ageForCalculation = _ageHistory.average()
        
        if (beautyForCalculation >= 4.3) {
            Dominance = (50 + ((beautyForCalculation - 4) * 10)).toInt()
        } else {
            Dominance = ((((beautyForCalculation) * 0.49) / 0.29)*10).toInt()
        }

        Sexual = when(ageForCalculation) {
            in 18..25 -> {
                (10*beautyForCalculation + 50).toInt()
            }
            in 25..35 -> {
                (10*beautyForCalculation + 40).toInt()
            }
            else -> {
                (10*beautyForCalculation + 30).toInt()
            }
        }
    }



}