# Panduan Optimasi Performa - Kartu Kompetensi Pegawai

## 1. Optimasi Layout

### Hindari Layout Bersarang
```xml
<!-- HINDARI -->
<LinearLayout>
    <LinearLayout>
        <LinearLayout>
            <!-- Konten -->
        </LinearLayout>
    </LinearLayout>
</LinearLayout>

<!-- GUNAKAN -->
<ConstraintLayout>
    <!-- Konten dengan constraints -->
</ConstraintLayout>
```

### Gunakan Merge Tag
```xml
<!-- view_competency_card.xml -->
<merge xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">
    <!-- Konten -->
</merge>
```

### Optimasi RecyclerView
```kotlin
// Di SkillAdapter
override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.setHasStableIds(true)  // Hindari rebind yang tidak perlu
    
    // Gunakan DiffUtil
    class SkillDiffCallback : DiffUtil.ItemCallback<Skill>() {
        override fun areItemsTheSame(oldItem: Skill, newItem: Skill) = 
            oldItem.id == newItem.id
        
        override fun areContentsTheSame(oldItem: Skill, newItem: Skill) = 
            oldItem == newItem
    }
}
```

## 2. Optimasi Memory

### Penggunaan ViewHolder Pattern
```kotlin
class SkillAdapter : RecyclerView.Adapter<SkillAdapter.ViewHolder>() {
    class ViewHolder(private val binding: ItemSkillBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        // Hindari findViewById berulang
        fun bind(skill: Skill) {
            binding.apply {
                skillName.text = skill.name
                skillScore.text = skill.score.toString()
                // ...
            }
        }
    }
}
```

### Lazy Loading Images
```kotlin
// Di InitialsAvatarView
private val avatarBitmap by lazy {
    // Buat bitmap hanya saat dibutuhkan
    createAvatarBitmap()
}
```

### Memory Leaks Prevention
```kotlin
class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    
    override fun onDestroy() {
        super.onDestroy()
        _binding = null  // Prevent memory leak
    }
}
```

## 3. Optimasi Database

### Indexing
```kotlin
@Entity(
    tableName = "skills",
    indices = [
        Index(value = ["employee_nip"]),
        Index(value = ["category"])
    ]
)
data class Skill(
    // ...
)
```

### Query Optimization
```kotlin
@Dao
interface SkillDao {
    // Gunakan LIMIT
    @Query("SELECT * FROM skills LIMIT 50")
    fun getRecentSkills(): LiveData<List<Skill>>
    
    // Gunakan WHERE untuk filter
    @Query("SELECT * FROM skills WHERE score >= :minScore")
    fun getHighScoreSkills(minScore: Int): LiveData<List<Skill>>
}
```

### Batched Operations
```kotlin
@Transaction
suspend fun updateSkills(skills: List<Skill>) {
    skills.chunked(50).forEach { batch ->
        skillDao.insertAll(batch)
    }
}
```

## 4. Background Processing

### WorkManager untuk Task Berat
```kotlin
class DatabaseBackupWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                // Backup database
                Result.success()
            } catch (e: Exception) {
                Result.retry()
            }
        }
    }
}
```

### Coroutines untuk Async Operations
```kotlin
class KompetensiRepository {
    suspend fun refreshData() = withContext(Dispatchers.IO) {
        try {
            // Refresh data dari server
            Result.success(data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

## 5. UI Responsiveness

### Smooth Animations
```kotlin
// Di CompetencyCardView
private fun animateProgress(newProgress: Int) {
    ObjectAnimator.ofInt(progressBar, "progress", currentProgress, newProgress)
        .apply {
            duration = 300
            interpolator = FastOutSlowInInterpolator()
            start()
        }
}
```

### Efficient List Updates
```kotlin
// Di SkillAdapter
fun updateSkills(newSkills: List<Skill>) {
    val diffCallback = SkillDiffCallback(skills, newSkills)
    val diffResult = DiffUtil.calculateDiff(diffCallback)
    
    skills = newSkills
    diffResult.dispatchUpdatesTo(this)
}
```

## 6. Network Optimization

### Caching
```kotlin
// Di build.gradle
implementation "com.github.bumptech.glide:glide:4.12.0"

// Di ImageView
Glide.with(context)
    .load(imageUrl)
    .diskCacheStrategy(DiskCacheStrategy.ALL)
    .into(imageView)
```

### Data Compression
```kotlin
// Di API calls
@Headers("Accept-Encoding: gzip")
@GET("api/skills")
suspend fun getSkills(): Response<List<Skill>>
```

## 7. Monitoring Tools

### Android Profiler
1. Memory Profiler:
   - Monitor heap dumps
   - Track memory leaks
   - Analyze garbage collection

2. CPU Profiler:
   - Record method traces
   - Analyze thread activity
   - Identify bottlenecks

3. Network Profiler:
   - Monitor network requests
   - Analyze payload sizes
   - Track response times

### StrictMode
```kotlin
if (BuildConfig.DEBUG) {
    StrictMode.setThreadPolicy(
        StrictMode.ThreadPolicy.Builder()
            .detectAll()
            .penaltyLog()
            .build()
    )
    
    StrictMode.setVmPolicy(
        StrictMode.VmPolicy.Builder()
            .detectLeakedSqlLiteObjects()
            .detectLeakedClosableObjects()
            .penaltyLog()
            .build()
    )
}
```

## 8. Best Practices

1. Lazy Initialization:
```kotlin
private val expensiveObject by lazy {
    // Initialize hanya saat dibutuhkan
    createExpensiveObject()
}
```

2. View Caching:
```kotlin
private var cachedView: View? = null

fun getView(): View {
    return cachedView ?: createView().also {
        cachedView = it
    }
}
```

3. Resource Management:
```kotlin
// Gunakan resource yang sesuai dengan device density
@DrawableRes
fun getResourceForDensity(density: Int): Int {
    return when (density) {
        DisplayMetrics.DENSITY_HIGH -> R.drawable.image_high
        DisplayMetrics.DENSITY_MEDIUM -> R.drawable.image_medium
        else -> R.drawable.image_low
    }
}
```

4. Bitmap Handling:
```kotlin
private fun loadBitmap(res: Resources, resId: Int): Bitmap {
    return BitmapFactory.Options().run {
        inJustDecodeBounds = true
        BitmapFactory.decodeResource(res, resId, this)
        
        inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)
        inJustDecodeBounds = false
        
        BitmapFactory.decodeResource(res, resId, this)
    }
}
