/*
 * Copyright (C) 2015 Hooked On Play
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package karacken.curl

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.animation.Interpolator

/**
 * AnimateCounter provides ability to animate the changing of numbers using the builtin
 * Android Interpolator animation functionality.
 */
class AnimateCounter private constructor(builder: Builder) {
    /**
     * Duration of animation
     */
    private val mDuration: Long
    /**
     * Initial value to start animation
     */
    private val mStartValue: Float
    /**
     * End value to finish animation
     */
    private val mEndValue: Float
    /**
     * Decimal precision for floating point values
     */
    private val mPrecision: Int
    /**
     * Interpolator functionality to apply to animation
     */
    private val mInterpolator: Interpolator?
    private var mValueAnimator: ValueAnimator? = null

    /**
     * Provides optional callback functionality on completion of animation
     */
    private var mListener: AnimateCounterListener? = null

    /**
     * Call to execute the animation
     */
    fun execute() {
        mValueAnimator = ValueAnimator.ofFloat(mStartValue, mEndValue)
        mValueAnimator!!.duration = mDuration
        mValueAnimator!!.interpolator = mInterpolator
        mValueAnimator!!.addUpdateListener { valueAnimator ->
            val current = java.lang.Float.valueOf(valueAnimator.animatedValue.toString())
            if (mListener != null) mListener!!.onValueUpdate(current)
            //     mView.setText(String.format("%." + mPrecision + "f", current));
        }

        mValueAnimator!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (mListener != null) {
                    mListener!!.onAnimateCounterEnd()
                }
            }
        })

        mValueAnimator!!.start()
    }

    class Builder {
        var mDuration: Long = 2000
        var mStartValue = 0f
        var mEndValue = 10f
        var mPrecision = 0
        var mInterpolator: Interpolator? = null

        /**
         * Set the start and end integers to be animated
         *
         * @param start initial value
         * @param end   final value
         * @return This Builder object to allow for chaining of calls to set methods
         */
        fun setCount(start: Int, end: Int): Builder {
            if (start == end) {
                throw IllegalArgumentException("Count start and end must be different")
            }

            mStartValue = start.toFloat()
            mEndValue = end.toFloat()
            mPrecision = 0
            return this
        }

        /**
         * Set the start and end floating point numbers to be animated
         *
         * @param start     initial value
         * @param end       final value
         * @param precision number of decimal places to use
         * @return This Builder object to allow for chaining of calls to set methods
         */
        fun setCount(start: Float, end: Float, precision: Int): Builder {
            if (Math.abs(start - end) < 0.001) {
                throw IllegalArgumentException("Count start and end must be different")
            }
            if (precision < 0) {
                throw IllegalArgumentException("Precision can't be negative")
            }
            mStartValue = start
            mEndValue = end
            mPrecision = precision
            return this
        }

        /**
         * Set the duration of the animation from start to end
         *
         * @param duration total duration of animation in ms
         * @return This Builder object to allow for chaining of calls to set methods
         */
        fun setDuration(duration: Long): Builder {
            if (duration <= 0) {
                throw IllegalArgumentException("Duration must be positive value")
            }
            mDuration = duration
            return this
        }

        /**
         * Set the interpolator to be used with the animation
         *
         * @param interpolator Optional interpolator to set
         * @return This Builder object to allow for chaining of calls to set methods
         */
        fun setInterpolator(interpolator: Interpolator): Builder {
            mInterpolator = interpolator
            return this
        }

        /**
         * Creates a [AnimateCounter] with the arguments supplied to this builder. It does not
         * [AnimateCounter.execute] the AnimationCounter.
         * Use [.execute] to start the animation
         */
        fun build(): AnimateCounter {
            return AnimateCounter(this)
        }
    }

    init {
        mDuration = builder.mDuration
        mStartValue = builder.mStartValue
        mEndValue = builder.mEndValue
        mPrecision = builder.mPrecision
        mInterpolator = builder.mInterpolator
    }

    /**
     * Stop the current animation
     */
    fun stop() {
        if (mValueAnimator!!.isRunning) {
            mValueAnimator!!.cancel()
        }
    }

    /**
     * Set a listener to get notification of completion of animation
     *
     * @param listener AnimationCounterListener to be used for callbacks
     */
    fun setAnimateCounterListener(listener: AnimateCounterListener) {
        mListener = listener
    }

    /**
     * Callback interface for notification of animation end
     */
    interface AnimateCounterListener {
        fun onAnimateCounterEnd()

        fun onValueUpdate(value: Float)
    }
}