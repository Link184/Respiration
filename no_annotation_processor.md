
Respiration repositories can be initialized without annotation processor, 
here you can find a example:

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
```
                
                
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
With respiration-compiler the problem is avoided by using RespirationRepository.USER_ID as user id
 in database children path
  