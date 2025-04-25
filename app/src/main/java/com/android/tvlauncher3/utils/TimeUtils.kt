package com.android.tvlauncher3.utils

import android.os.Build
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class TimeUtils {
    companion object {
        enum class DateType {
            yyyyMM, yyMM, MMdd, yyyyMMdd, yyMMdd
        }

        enum class TimeType {
            HHmm, mmss, HHmmss
        }

        fun getDate(type: DateType): String {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val now = LocalDateTime.now()
                val formatter: DateTimeFormatter
                when (type) {
                    DateType.yyyyMM -> {
                        formatter = DateTimeFormatter.ofPattern("yyyy-MM")
                    }

                    DateType.yyMM -> {
                        formatter = DateTimeFormatter.ofPattern("yy-MM")
                    }

                    DateType.MMdd -> {
                        formatter = DateTimeFormatter.ofPattern("MM-dd")
                    }

                    DateType.yyyyMMdd -> {
                        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    }

                    DateType.yyMMdd -> {
                        formatter = DateTimeFormatter.ofPattern("yy-MM-dd")
                    }
                }
                return now.format(formatter)
            } else {
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                val stringBuffer = StringBuffer()
                stringBuffer.append(year)
                stringBuffer.append('-')
                stringBuffer.append(month)
                stringBuffer.append('-')
                stringBuffer.append(day)
                return stringBuffer.toString()
            }
        }

        fun getTime(type: TimeType): String {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val now = LocalDateTime.now()
                val formatter: DateTimeFormatter
                when (type) {
                    TimeType.HHmm -> {
                        formatter = DateTimeFormatter.ofPattern("HH:mm")
                    }

                    TimeType.mmss -> {
                        formatter = DateTimeFormatter.ofPattern("mm:ss")
                    }

                    TimeType.HHmmss -> {
                        formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
                    }
                }
                return now.format(formatter)
            } else {
                val calendar = Calendar.getInstance()
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)
                val second = calendar.get(Calendar.SECOND)
                val stringBuffer = StringBuffer()
                when (type) {
                    TimeType.HHmm -> {
                        stringBuffer.append(hour)
                        stringBuffer.append(':')
                        stringBuffer.append(minute)
                    }

                    TimeType.mmss -> {
                        stringBuffer.append(minute)
                        stringBuffer.append(':')
                        stringBuffer.append(second)
                    }

                    TimeType.HHmmss -> {
                        stringBuffer.append(hour)
                        stringBuffer.append(':')
                        stringBuffer.append(minute)
                        stringBuffer.append(':')
                        stringBuffer.append(second)
                    }
                }
                return stringBuffer.toString()
            }
        }
    }
}