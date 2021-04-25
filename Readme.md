# Voice Recording App : MVVM

> A voice recording app for basic demonstration of latest best practices of Android.

 - Using : _Kotlin, Navigation Components, Room Database (for caching), Lifescycle, Dependency Injection: HILT, Kotlin Coroutines, Android Material Components_



#### Basic Code Explanation

- targetSDK/compileSDK : Android 10, used flag `requestLegacyExternalStorage` for normal storage access. (Android 10 and above requires usage of ScopedStorage and MediaStore) these docs are helpful : [DataStorage](https://developer.android.com/training/data-storage) & [Storage Updates ](https://developer.android.com/about/versions/11/privacy/storage)

- Hilt construction injection and field injection are used wherever required, through the `AppModule`, `ActivityModule(AcitivityScoped)`, and `DatabaseModule` for Room. [Hilt Documentation](https://developer.android.com/training/dependency-injection/hilt-android) is a good source to get started.

- Priamry DataType created for saving voice record is `Record`: with basically two properties one `title` and other is `filePath` linked to the voice recorded file. (third one is the `id` as a [@PrimaryKey](https://developer.android.com/training/data-storage/room/defining-data) for Room Database)

- `AppDatabase` extending `RoomDatabase` acts as the primary Database, implemented using the `RoomDatabase.Callback` for accessing data in background thread using coroutines. A basic guide for the same is [this aticle](https://www.raywenderlich.com/7414647-coroutines-with-room-persistence-library) by Ray Wenderlich, but is without DI.
  
- Used new ListAdapter a better version of `RecyclerView.Adapter` imporoves use case for working with LiveData and Coroutines, by comparing the data that is already laoded. Beter Explanation [here](https://developer.android.com/reference/androidx/recyclerview/widget/ListAdapter) and this [article](https://blog.usejournal.com/why-you-should-be-using-the-new-and-improved-listadapter-in-android-17a2ab7ca644) by Ferdinand Bada. [DiffUtil](https://developer.android.com/reference/androidx/recyclerview/widget/DiffUtil)

- Not all functions need to be suspend type, some tasks are better on main thread (playing/recording audio), while some tasks like accessing data from Database on background thread.

- No Services used, the app is intended to work well on the foreground only.

- Some other Helpfull links : [ViewModels](https://developer.android.com/topic/libraries/architecture/viewmodel), [ViewBinding](https://developer.android.com/topic/libraries/view-binding)