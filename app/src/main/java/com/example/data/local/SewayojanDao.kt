package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SewayojanDao {
    // Jobs
    @Query("SELECT * FROM job_opportunities")
    fun getAllJobs(): Flow<List<JobOpportunity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertJobs(jobs: List<JobOpportunity>)

    @Query("DELETE FROM job_opportunities")
    suspend fun clearJobs()

    @Transaction
    suspend fun replaceJobsInTx(jobs: List<JobOpportunity>) {
        clearJobs()
        insertJobs(jobs)
    }

    @Update
    suspend fun updateJob(job: JobOpportunity)

    // Admit Cards
    @Query("SELECT * FROM admit_cards")
    fun getAllAdmitCards(): Flow<List<AdmitCardItem>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAdmitCards(cards: List<AdmitCardItem>)

    @Query("DELETE FROM admit_cards")
    suspend fun clearAdmitCards()

    @Transaction
    suspend fun replaceAdmitCardsInTx(cards: List<AdmitCardItem>) {
        clearAdmitCards()
        insertAdmitCards(cards)
    }

    @Update
    suspend fun updateAdmitCard(card: AdmitCardItem)

    // Results
    @Query("SELECT * FROM exam_results")
    fun getAllExamResults(): Flow<List<ExamResultItem>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExamResults(results: List<ExamResultItem>)

    @Query("DELETE FROM exam_results")
    suspend fun clearExamResults()

    @Transaction
    suspend fun replaceExamResultsInTx(results: List<ExamResultItem>) {
        clearExamResults()
        insertExamResults(results)
    }

    @Update
    suspend fun updateExamResult(result: ExamResultItem)

    // User Applications
    @Query("SELECT * FROM user_applications")
    fun getUserApplications(): Flow<List<UserApplication>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(app: UserApplication)
}
