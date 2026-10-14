package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Habit
import com.example.data.model.HabitLog
import com.example.data.model.HabitStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Database(
    entities = [Habit::class, HabitLog::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class StreqaDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao

    companion object {
        @Volatile
        private var INSTANCE: StreqaDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): StreqaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StreqaDatabase::class.java,
                    "streqa_habits_database.db"
                )
                    .addCallback(StreqaDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class StreqaDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.habitDao())
                }
            }
        }

        private suspend fun populateInitialData(dao: HabitDao) {
            val today = LocalDate.now()
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE
            val startMonth = today.minusDays(14).format(formatter)
            val endMonth = today.plusDays(16).format(formatter)

            // Seed habits inspired directly by the user's screenshots
            val h1 = Habit(
                title = "تداول",
                description = "صفقة الى صفقتين فاليوم بربح 0.5%",
                iconEmoji = "💸",
                colorHex = "#2DD4BF", // Teal
                startDate = startMonth,
                endDate = endMonth,
                frequencyLabel = "الاثنين · الثلاثاء · الأربعاء · الخميس · الجمعة",
                scheduledDays = "2,3,4,5,6", // Mon-Fri
                isFavorite = true
            )
            val id1 = dao.insertHabit(h1)

            val h2 = Habit(
                title = "أذكار الصباح",
                description = "بداية اليوم بذكر الله والطمأنينة",
                iconEmoji = "☀️",
                colorHex = "#38BDF8", // Sky Blue
                startDate = startMonth,
                endDate = endMonth,
                frequencyLabel = "يومي",
                scheduledDays = "1,2,3,4,5,6,7",
                isFavorite = true
            )
            val id2 = dao.insertHabit(h2)

            val h3 = Habit(
                title = "أذكار المساء",
                description = "حصن المساء والسكينة",
                iconEmoji = "🌙",
                colorHex = "#F472B6", // Pink/Rose
                startDate = startMonth,
                endDate = endMonth,
                frequencyLabel = "يومي",
                scheduledDays = "1,2,3,4,5,6,7",
                isFavorite = false
            )
            val id3 = dao.insertHabit(h3)

            val h4 = Habit(
                title = "دوام يومي من ٨ إلى ٣",
                description = "الالتزام والإنتاجية في العمل",
                iconEmoji = "⭐",
                colorHex = "#FBBF24", // Amber
                startDate = startMonth,
                endDate = endMonth,
                frequencyLabel = "الأحد · الاثنين · الثلاثاء · الأربعاء · الخميس",
                scheduledDays = "1,2,3,4,5",
                isFavorite = true
            )
            val id4 = dao.insertHabit(h4)

            val h5 = Habit(
                title = "مذاكرة",
                description = "تطوير نفسي واكتساب مهارات جديدة",
                iconEmoji = "📚",
                colorHex = "#FB7185", // Coral
                startDate = startMonth,
                endDate = endMonth,
                frequencyLabel = "الثلاثاء · الأربعاء · الخميس · الجمعة · السبت",
                scheduledDays = "3,4,5,6,7",
                isFavorite = false
            )
            val id5 = dao.insertHabit(h5)

            val h6 = Habit(
                title = "اسقي النبتات",
                description = "لا يموتون عيالي 🪴",
                iconEmoji = "🪴",
                colorHex = "#34D399", // Emerald
                startDate = startMonth,
                endDate = endMonth,
                frequencyLabel = "مرة واحدة في الأسبوع",
                scheduledDays = "7", // Saturday
                isFavorite = false
            )
            val id6 = dao.insertHabit(h6)

            val h7 = Habit(
                title = "تمرين",
                description = "جسم صحي ونشاط مستمر",
                iconEmoji = "🏋️‍♂️",
                colorHex = "#2DD4BF",
                startDate = startMonth,
                endDate = endMonth,
                frequencyLabel = "الأحد · الاثنين · الأربعاء · الخميس · الجمعة",
                scheduledDays = "1,2,4,5,6",
                isFavorite = false
            )
            val id7 = dao.insertHabit(h7)

            val h8 = Habit(
                title = "البقرة",
                description = "للبركة والتوفيق",
                iconEmoji = "💖",
                colorHex = "#F472B6",
                startDate = startMonth,
                endDate = endMonth,
                frequencyLabel = "يومي",
                scheduledDays = "1,2,3,4,5,6,7",
                isFavorite = false
            )
            val id8 = dao.insertHabit(h8)

            // Seed some past completed and failed days for realism matching screenshots
            val d0 = today.format(formatter)
            val d1 = today.minusDays(1).format(formatter)
            val d2 = today.minusDays(2).format(formatter)
            val d3 = today.minusDays(3).format(formatter)

            // Habit 1
            dao.upsertLog(HabitLog(habitId = id1, date = d0, status = HabitStatus.DONE, notes = "صفقتين بنجاح 0.6%"))
            dao.upsertLog(HabitLog(habitId = id1, date = d1, status = HabitStatus.DONE, notes = "صفقة واحدة بربح ممتاز"))
            dao.upsertLog(HabitLog(habitId = id1, date = d2, status = HabitStatus.DONE, notes = "التزام تام بإدارة رأس المال"))

            // Habit 2
            dao.upsertLog(HabitLog(habitId = id2, date = d0, status = HabitStatus.DONE, notes = "بعد صلاة الفجر مباشرة"))
            dao.upsertLog(HabitLog(habitId = id2, date = d1, status = HabitStatus.DONE, notes = "في طريقي للعمل"))
            dao.upsertLog(HabitLog(habitId = id2, date = d2, status = HabitStatus.DONE, notes = "الحمد لله"))

            // Habit 3
            dao.upsertLog(HabitLog(habitId = id3, date = d0, status = HabitStatus.DONE, notes = "قبل المغرب"))
            dao.upsertLog(HabitLog(habitId = id3, date = d1, status = HabitStatus.DONE, notes = "في وقتها المحدد"))

            // Habit 4
            dao.upsertLog(HabitLog(habitId = id4, date = d1, status = HabitStatus.DONE, notes = "إنجاز جميع المهام المطلوبة"))

            // Habit 5
            dao.upsertLog(HabitLog(habitId = id5, date = d0, status = HabitStatus.DONE, notes = "قراءة فصل كامل وحل تمرينين"))
            dao.upsertLog(HabitLog(habitId = id5, date = d1, status = HabitStatus.DONE, notes = "مراجعة تلخيص الأسبوع"))

            // Habit 7
            dao.upsertLog(HabitLog(habitId = id7, date = d0, status = HabitStatus.DONE, notes = "تمرين أكتاف وصدر عالي الكثافة"))
            dao.upsertLog(HabitLog(habitId = id7, date = d1, status = HabitStatus.DONE, notes = "كارديو 30 دقيقة"))
            dao.upsertLog(HabitLog(habitId = id7, date = d2, status = HabitStatus.FAILED, notes = "كنت مجهد وضيق في الوقت"))
        }
    }
}
