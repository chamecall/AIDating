package org.corpitech.vozera.neurobiology

import android.util.Log
import java.util.*
import kotlin.random.Random

class Neurobiology(_sex: Int, _age: Int, _beauty: Float) {
    var Dominance = 0
    var Sexual = 0
    var Resource = 0
    var Social = 0

    val Value: Int
        get() {
            return (Dominance + Sexual + Resource + Social) * 999 / 396 // 4*99
        }

    private val _sexHistory: MutableList<Int> = mutableListOf();
    private val _ageHistory: MutableList<Int> = mutableListOf();
    private val _beautyHistory: MutableList<Float> = mutableListOf();

    init {
        updateScores(_sex, _age, _beauty)
    }

    fun getScores(): Array<Float> {
        Log.i("TEST", "$Dominance $Resource $Social $Sexual")
        return arrayOf(Dominance / 100.0f, Resource / 100f, Social / 100f, Sexual / 100f)
    }

    fun updateScores(_sex: Int, _age: Int, _beauty: Float?) {
        if (_beauty != null)
            _beautyHistory.add(_beauty)

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



        val beautyForCalculation = _sexHistory.average()
        val ageForCalculation = _ageHistory.average()


        Dominance = if (beautyForCalculation >= 4.3) {
            (50 + ((beautyForCalculation - 4) * 10)).toInt()
        } else {
            ((((beautyForCalculation) * 0.49) / 0.29) * 10).toInt()
        }

        Sexual = when (ageForCalculation) {
            in 18.0..25.0 -> {
                (10 * beautyForCalculation + 50).toInt()
            }
            in 25.0..35.0 -> {
                (10 * beautyForCalculation + 40).toInt()
            }
            else -> {
                (10 * beautyForCalculation + 30).toInt()
            }
        }

        Resource = (8 * beautyForCalculation + (80 - when(ageForCalculation){
            in 18.0..25.0 -> 50
            in 25.0..35.0 -> 40
            else -> 30
        })).toInt()

        Social = Random(Calendar.getInstance().time.time).nextInt(0,100)
    }


}