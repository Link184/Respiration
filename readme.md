Respiration
=====

Respiration is a light library which centralizes firebase database logic. It is a join of Firebase 
and rxJava libraries to give you a easier and shorter implementation of Firebase library where you don't
need to care about verbose ValueEventListener addiction or removal. 

Library is designed to work with POJO data models which simplify the code. No more primitives, 
casts or HashMaps inside a business logic.

Gradle
--------
You can download a jar from GitHub's [releases page][3].

Gradle:

```gradle
repositories {
    jcenter()
}
 
dependencies {
    compile 'com.link184:respiration:0.1.4'
}
```

Maven:

```maven
<dependency>
  <groupId>com.link184</groupId>
  <artifactId>respiration</artifactId>
  <version>0.1.4</version>
  <type>pom</type>
</dependency>
```

ProGuard
--------
By using this library is assumed that you put all your models as exceptions into proguard rules.
```pro
########--------Respiration--------#########
-keepattributes Signature
-keepattributes *Annotation*
-keepclassmembers class <<<path.to.your.models>>>.** {
  *;
}
```


How do I use Respiration?
-------------------
Usage is simple like inspiration or expiration =)

Simple use cases with Respiration's  will look something like this:

```java
// Init a repository:
FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
GeneralRepository samplePrivateRepository = new GeneralRepository.Builder<>(SamplePrivateModel.class,
            SAMPLE_PRIVATE_CHILD, currentUser != null ? currentUser.getUid() : null)
            .setAccessPrivate(true) // Data are available only for authenticated users.
            .setPersistence(true) // Set firebase db to be available offline
            .build();
 
// Attach a subscriber to handle all data changes from firebase. SubscriberFirebase is a rxJava 
// DisposableObserver so you can dispose or reatach it anytime.
samplePrivateRepository.subscribe(new SubscriberFirebase<SamplePrivateModel>() {
            @Override
            public void onSuccess(SamplePublicModel samplePublicModel) {
                nameContainer.setVisibility(View.VISIBLE);
                Log.d(TAG, "Found my name" + samplePublicModel.getName());
            }
 
            //Optional override
            @Override
            public void onFailure(Throwable error) {
                //Some logic when fail.
                nameContainer.setVisibility(View.GONE);
                Log.e(TAG, "Fail! ", error);
            }
        });
```

What about firebase arrays
-----
With ListRepository you can easily wrap firebase Map<String, T> to List<T>
```
FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
ListRepository<SampleFriendModel> listRepository = new ListRepository.Builder<>(SampleFriendModel.class)
                .setChildren(SAMPLE_FRIENDS_CHILD, currentUser != null ? currentUser.getUid() : null)
                .setAccessPrivate(true)
                .setPersistence(true)
                .build();
  
listRepositorySubscriber = new SingleSubscriberFirebase<List<SampleFriendModel>>() {
            @Override
            public void onSuccess(List<SampleFriendModel> dataSnapShot) {
                for (SampleFriendModel friend : dataSnapShot) {
                    Log.e(TAG, "onSuccess: " + friend.toString());
                }
            }
        };
listRepository.subscribeToList(listRepositorySubscriber);
//Also you can subscribe to specific item from list
listRepository.subscribeToItem("someKey", listRepositorySubscriber);
```

Unleash all reactive power
----
If you need more specific behavior you can easily extend a respiration repository or just call 
asObservable() method.
```
myRepository.asObservale()
        .map(this::mapToRealmObject)
        .filter(FilterUtils::removeOutdatedSamples)
        .subscribe(...);
```

Don't forget to subscribe/unsubscribe according to android lifecycle.
```
@Override
    protected void onResume() {
        super.onResume();
        SubscriberFirebase subscriber = new SubscriberFirebase<Model>() { ... };
        privateRepository.subscribe(subscriber);
    }
 
    @Override
    protected void onPause() {
        subscriber.dispose();
        super.onPause();
    }
```

Also you can use SingleSubscriberFirebase class to obtain a value just once without subscription 
to data changes.

**Note** If user is not authenticated and you set setAccessPrivate(true) and database reference 
path may include user id then you must reset database references path. 
Just call samplePrivateRepository.resetRepository(), here is a example:
```
samplePrivateRepository.getFirebaseAuth()
        .signInWithEmailAndPassword("sample@sample.sample", "password")
        .addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                privateRepository.resetRepository(FirebaseModule.SAMPLE_PRIVATE_CHILD,
                        privateRepository.getFirebaseAuth().getCurrentUser().getUid());
            }
        });
```

Sample
-------
For more details you can clone and assemble a sample project from this github repository.
Here is a database structure dump from sample project [DATABASE_JSON][2]. Import it into your 
project and enjoy the library.

License
-------
See the [LICENSE][1] file for details.

[1]: https://github.com/Link184/Respiration/blob/master/LICENSE
[2]: https://github.com/Link184/Respiration/blob/master/firebase_database.json
[3]: https://github.com/Link184/Respiration/releases
