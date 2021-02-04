# AndroidCodingChallenge

## My BaseProject

The app is written in 100% Kotlin code. I used my personal BaseProject that I use for every project I start. 
A little more on my personal BaseProject. It has MVVM layers set up and this is my package structure:
 - "data" package: Used for handling data section of the app, in this package I usually have: 
    - "repo" package for repository classes
    - "remote" package for data classes that handle fetching data from Internet
 I omitted "db" package with Room implementation and DAO classes from my BaseProject because not every project needs those. 
 For example I thinkthat this CodingChallenge didn't need some data persistance or caching logic.
 
 - "di" package: package for Dependency Injection classes, since I use Koin in my BaseProject I put AppModeule.kt class that
 handles modules

 - "shared" package: I put things that are going to be used "app wide" in this package, it has:
    - "base" subpackage for  BaseDataSource and Resource classes
    - "kotlin" package where I place classes that contain extension functions that I use for every project (int.toDp(), showViewWithAnimation(), changeBackgroundColorWithoutChangingTheDrawableShape()...)
    - "util" package  for utility classes like PreferenceCache or SettingsManager
    - "widgets" package for custom views classes
 
 - "ui" package: used for handling UI. In this package I keep Fragments, Activities, Adapters, and ViewModels
 
 - "vo" package: Value Objects package for keeping data classes and models
 
 In my BaseProject I also have libraries that I use in almost every project, like Timber, Retrofit, Glide, LoggingInterceptor,
 Architecture Components and so on... 
 
 Now, about the CodingChallenge. 
 
 ## AndroidCodingChallenge
 
 ### UI

 I used NavigationComponent with 3 screens (4 if you count Landscape version an additional screen) in the app: MainScreen with Landscape support, DetailsSceen and Settings screen. First is the **MainFragment.kt** that 
 displays the employees in small circles (inspied by Instagram stories) and you can scroll left and right to pick an employee
 who is then displayed in the circle in center of the screen. I used HorizontalRecyclerView with **EmployeeAdapter.kt** for doing this. Each team has it's own color that is picked at the app startup. 
 Beneath the scrolling list there is a toggle button that lets you choose between snapping or smooth scrolling. I did this by simply setting and removing SnapHelper from recycler, also the ToggleButton is custom made by me using MotionLayout.
In the bottom right there is an arrow that indicates left. When users touch that arrow it displays expanding list of team colors so they know which color is which team. This is also a small recycler with **TeamsAdapter.kt** which I placed in FrameLayout embedded in MotionLayout and I display it by moving constraint with MotionLayout transitions.
Bottom left there is a button that takes you to **SettingsFragment.kt** screen. I implemented Night mode toggling and Language changing logic, you can pick either English or Montenegrin for the app language.

When the users tap the big image placed in the center of the screen it takes them to **DetailsFragment.kt**  screen, where details about employee are displayed. I used MotionLayout for this screen and the user needs to scroll up to see text information about the employee. I also colored the tabs in employees team colors for every employee that is displayed.

Landscape mode is supported. When the users put the phone in landscape the recycler is displayed in GridLayoutManager mode and they see a vertical Grid recycler with Header logic implemented. I used **GridEmployeeAdapter.kt** for handling Landscape mode recycler display. I made another data class **GridEmployee.kt** for handling header logic by setting the ViewType (either HeaderView or DataView) field inside the data class and then handling header displaying in **GridEmployeeAdapter.kt**.
In Landscape mode there is no arrow that displays teams information because the information is displayed in GridRecycler. I used constraintLayout horizontal bias to display GridRecycler at 0.45 of the screen so the other 0.55 is
used for main employee image in a circle. Right side of the screen contains NestedScrollView so the user can scroll down and see employee information. Users can't go to **DetailsFragment.kt** from landscape mode since all the data is displayed already so I removed the listener, and also landscape mode is not supported once you're in **DetailsFragment.kt**.
I made data persistant by using **MainFragmentViewModel.kt** so when you rotate the screen the same employee image and will be loaded in main image circle.
I think that covers the UI parts of the app.

### Data

About the data. The JSON response that I got from https://teltech.co/teltechiansFlat.json has incomplete image url values. Through Teltech website inspection I found out that the format of image urls is
https://teltech.co/images/members/employee-main.jpg for main employee image
https://teltech.co/images/members/employee.jpg for small employee image.
The main problem was that small employee image comes as a combination of main and small image. I needed to cut the image in half and only use left side of the image. I handled all that inside my project. I load the small image url into bitmap using URL.toBitmap() extension function which decodesStream, then I split the bitmap in half and use the first half for small employee image.

In **MainViewModel.kt** the data is fetched from EmployeeRepository.kt class inside a coroutine liveData{} builder. Then the image loading is done for every employee. Also I get a key-value pair of "teamName"-"color" and place that in "colorHashMap" for making TeamsRecycler that displays team colors. At every 8th  step of for each loop I use "liveData.emit()" to emit the current list to the recycler so that the users don't need to wait for loading of the data, and the data is not loaded all at once. I picked 8 because the screen fits 5 employees and maybe 8 get bound in **EmployeeAdapter.kt** at once.

Since the **EmployeeAdapter.kt** is receiving multiple data updates in matter of seconds, I decided to use itemDiffer of DiffUtil class and update the recycler data in parts and stop the adapter of reloading already loaded employees.

## Aditional tasks

The app has **LiveDataNetworkMonitor.kt** class in "util" package that is used for monitoring if the phone is connected to the internet. I combined LiveData along with ConnectivityManager to create a "live" boolean which returns true for when there is connection and false otherwise. In **MainFragmentViewModel.kt** I keep a "internetConnectionFlag" which is a MutableLiveData<Boolean> connected to LiveDataNetworkMonitor.class that gets notified whenever there is a change in internet connection. In **MainFragment.kt**  there is an observer watching "internetConnectionFlag" and whenever user gets disconnected from the internet a **MessageDialogFragment.kt** is displayed which notifies the user about loss of connection.

As task required I prepared an event tracking service. FirebaseAnalytics is initialized in BaseApp.kt class, I chose to only initialize FirebaseAnalytics once and use it everywhere that's why I initialized it in **BaseApp.kt**. You can reference it anywhere in the app by using "BaseApp.firebaseAnalytics.event(...".

Since my experience with Unit testing in Android is very limited I did not do Unit testing for this project. I made a choice of displaying my true knowlege rather than
mocking (intentional pun) my testing knowlege. 
