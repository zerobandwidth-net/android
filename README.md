## zer0bandwidth.net Android Library ##

This Android library provides several useful features across a wide variety of
subjects. It is organized to parallel the package structure of Android itself,
such that any library classes that are closely related to standard Android
classes will reside in a roughly-parallel package structure.

For more information about the features of the library, consult its [GitHub
Pages](http://zerobandwidth-net.github.io/android/) or its
[JavaDoc](http://zerobandwidth-net.github.io/android/javadoc/index.html).

### Packages and Features ###

* `app` provides utility classes for dealing with the application itself.
    * `AppUtils` provides shorthands for common tasks such as obtaining
      the app's name and version as a string, initializing an activity's "back"
      button, and analyzing text direction.
    * `Managers` is a pre-API23 implementation of an Android manager factory;
      given a context and a class, it will return an instance of that manager
      class. This is exactly how the `getSystemService(Class)` method works in
      the API 23+ version of `Context`.
* `content` provides utility classes for working with content.
    * `ContentUtils` provides shorthands for copying text to the clipboard,
      obtaining the clipboard manager, dispatching text to the Android OS's
      "share" infrastructure, and directly initiating Twitter messages.
    * `IntentUtils` provides shorthands for creating and working with intents.
    * `PreferencePortal` implements an alternative API for marshalling
      application preferences, including a feature allowing the app to marshal
      integer values as string preferences. This particular feature works around
      peculiarities in the behavior of integer preferences under certain
      conditions.
* `database` provides the `SQLitePortal` extension of `SQLiteOpenHelper`. The
  class defines many semantic constants used in SQLite transactions and specific
  data values. The class also exposes a more intuitive API for managing database
  connections asynchronously. A further extension, `SQLiteAssetPortal`, allows
  for creation of static, read-only databases that are marshalled from asset
  files upon installation.
* `database.querybuilder` provides a fluid API and grammar for constructing and
  executing queries, hiding the cumbersome syntax of the various 
  `SQLiteDatabase` methods behind a "builder"-like grammar.
* `database.sqlitehouse` is a suite of classes and annotations which allow an
  app developer to define a SQLite database by decorating the Java object
  classes that will contain that database's row data. The main class, which is
  itself an extension of the library's `SQLitePortal`, manages its own creation,
  updates, connections, and even insert/update/search queries, using functions
  that reflexively examine the schematic classes, and use those classes directly
  for input and output. See the JavaDoc for `SQLiteHouse` for further usage
  details and examples.
* `nonsense` is a project to implement classes that randomly generate
  intelligible nonsense. Each class uses a different algorithm to generate its
  own flavor of nonsense, but they all share a common interface, and can thus
  be traded in and out of an app that might choose between multiple generators.
  The original implementation, `NonsenseBuilder`, is the basis of the
  zerobandwidth app "Poppycock".
* `security` provides classes related to application security functions.
    * `PermissionCheckpoint` allows an app developed for Android 5 and later to
      preemptively request the user's confirmation of all permissions that are
      required for the app's function. This works around the fundamental changes
      to the permissions model in which permissions can no longer be granted at
      installation time.
* `services` provides both classes to aid in interaction with services, and also
  some services of its own.
    * `SimpleServiceConnection` is a canonical implementation of a service
      connection and listener class. This saves app developers from having to
      implement the same boilerplate connection code for each service.
    * `SingletonService` is exactly what it claims to be &mdash; a service which
      stores singleton instances of various classes, which can be accessed and
      replaced via a straightforward API.
* `telephony` provides access to Android telephony functions.
    * `TelephonyController` re-exposes access to various fundamental control
      functions of the Android device's telephony features, which were slowly
      deprecated, obscured, or obstructed by various Android releases over time.
* `text.format` provides specialized text formatters.
    * `TitleFormatter` re-capitalizes the words of a string based on rules
      for capitalizing titles.
* `ui` provides implementations of interesting user interface features.
    * `MultitapAlertDialog`, and its app-compat variant
      `MultitapAlertCompatDialog`, are dialog windows that require multiple
      taps on the confirmation button before taking action. This is useful when
      an app needs to ask the user to confirm some significant action, like
      deletion of data.
* `view.updaters` provides convenient mechanisms for updating UI views at
  runtime.
    * `MenuItemUpdater` allows an activity to update the icon and/or text of a
      menu item dynamically in response to runtime conditions.

### Using the Library ###

Clone this repository to your development environment, and then add the
following to your global `gradle.properties` file:

```lib_zeroAndroid=/full/path/to/local/zerobandwidth/android/android_lib/```

In the app's `settings.gradle`, ensure that a mapping to the module is included:

```
    include 'lib_zeroAndroid'
    project('lib_zeroAndroid').projectDir = new File( lib_zeroAndroid )
```

Then, in the app's `build.gradle`, add a dependency on that module:

```
    dependencies {
        // ...
        compile project(':lib_zeroAndroid')
    }
```

### Automated Testing ###

The library includes a thorough unit-testing suite which covers a majority of
the extant code. To set up automated testing in Android Studio, follow these
steps:

1. Tap the build configuration list and select **Edit Configurations**.
2. Create a configuration of type **Android Instrumented Tests**.
3. Edit the following settings on the **General** tab:
    1. **Module** = **`android_lib`**
    2. **Test:** = **All in package**
    3. **Package:** = **`net.zer0bandwidth.android.lib`**
4. In the **Before launch:** task list, add a new item for **Run Gradle Task**.
    1. **Gradle Project:** &rarr; *(folder icon)* = **`android:android_lib`**
    2. **Tasks:** = **`createDebugCoverageReport`**
5. Save the new build configuration.
6. Execute the new build configuration. You should see a 
   `createDebugCoverageReport` task being executed before the unit tests run.

To have an additional configuration that doesn't generate a coverage report,
make a copy of the configuration described above, and then remove the **Run
Gradle Task** pre-build step.

### The Library's Base Domain ###

In November 2018, in a staggering display of derpitude, we missed the renewal
notification for the original `zerobandwidth.net` domain, and it was seized by
Chinese domain squatters. This precipitated an emergency refactor and re-release
of the library, which was tracked as issue #58 and released in version 0.3.0,
and changed the base domain to `zer0bandwidth.net`, which we will be watching
more closely from now on. This is one of those "lessons that stick with you",
folks — always route the domain registration emails to your primary email
account, not the one you use for junk mail.

