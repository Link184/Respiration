Welcome to offline firebase
===
This is a module which will simulate firebase on your device but will work totally disconnected form
 firebase services and internet. Local database will be saved in android local dir in json format
 (is not really json, by firebase rules there are no json arrays, only objects). Local data base 
 can be loaded from .json file located in assets resources, this is useful for first time db 
 initialization. Also db can be reset in runtime by calling yourLocalRepository.resetRepository(new localConfig)
 
Main reason why I decided to write offline firebase module
---
It works in China. If you have a big app which you want to launch in china markets
you must remove all your firebase logic because almost all google services are forbidden out there.
This module is a solution to keep your firebase code work without firebase services. So you will
keep the same respiration api with the same functionality as with firebase but without it. The only thing
you must to do is to declare differently respiration initializations(FirebaseRepository or LocalRepository) 


How to use offline firebase
---

Gradle:

```gradle
repositories {
    jcenter()
}
 
dependencies {
    implementation 'com.link184:respiration-core:0.6.9'
    implementation 'com.link184:respiration-local:0.6.9'
    annotationProcessor 'com.link184:respiration-compier:0.6.9'

    //gson and rxJava dependecies
    ...
}
```

if you want to use respiration local and firebase modules together you must exclude some junk modules

```gradle
    configurations {
        all*.exclude group: 'com.link184', module: 'respiration-firebase-junk'
    }
```

Is the same api like in respiration firebase repositories bot is different initialization.

Module initialization
```java
@RespirationModule
public class CustomModule {
    private final String TEST_ASSET_DB_NAME = "user_db.json";

    //Configure repositories with annotations
    @LocalRepository(dataSnapshotType = User.class,
                children = {"userData", "user"},
                dataBaseAssetPath = TEST_ASSET_DB_NAME)
    public LocalGeneralRepository samplePrivateRepository;


    @LocalRepository(dataSnapshotType = Vector.class)
    public LocalGeneralRepository myRepo2;
}
```

@LocalRepository also works on classes. Single rule is to extend class from LocalGeneralRepository 
or ListRepository

```java
@LocalRepository(dataSnapshotType = SamplePrivateModel.class,
        children = {"children1", "child2", RespirationRepository.USER_ID, "child3"})
public class CustomRepository extends LocalGeneralRepository<SamplePrivateModel> {
    public CustomRepository(Configuration<SamplePrivateModel> repositoryConfig) {
        super(repositoryConfig);
    }

    public void testMethod() {
        Log.d(TAG, "testMethod: ");
    }
}
```

Generated object can be accessed by generated {AnnotatedClassName}Builder class.

Local repositories use android context to access asset resorces. So we must pass context to get 
instance of our repository. Don't worry about memory leaks, repository dont keep context inside and use it
  only on first initialization.
  
```
CustomRepository customRepository = CustomRepositoryBuilder.getInstance(context);
customRepository.testMethod();
```
