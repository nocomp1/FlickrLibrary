# FlickrLibrary
An easy to use library for authenticating a user retrieving photo galleries and uploading photos to flickr. 

# minSdkVersion 23

#Get started
Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
Step 2. Add the dependency

```
	dependencies {
	        implementation 'com.github.nocomp1:FlickrLibrary:0.10'
	}
```

Step 3 Inside your Manifest add permissions launch mode and intent-filters

```
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>

<application. ...
	
	<activity 
            android:documentLaunchMode="intoExisting"
            >
	    ....
	    ....
	    
	     <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
                <data
                    android:host="callback"
                    android:scheme="flickr"/>
            </intent-filter>
	    
	    ....
	    ....
        </activity>
	    
```

In MVVM pattern inside your ViewModel implement the Flickr.FlickrOauthListener.
```
import androidx.lifecycle.ViewModel
import com.example.flickrlibrary.Flickr
import com.example.flickrlibrary.model.Galleries

class FlickrLibraryExampleViewModel: ViewModel(), Flickr.FlickrOauthListener {
    //Initialize the listener
    private var flickr: Flickr = Flickr(this)
    
    override fun authenticationSuccess(results: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onError(throwable: Throwable) {
        TODO("Not yet implemented")
    }

    override fun photoGalleries(galleries: Galleries) {
        TODO("Not yet implemented")
    }

    override fun photoUrlList(urls: List<String>) {
        TODO("Not yet implemented")
    }

    override fun requestToken(token: String) {
        TODO("Not yet implemented")
    }
}

```

# To authorize a user
In order to get authorized first we need to pass a request token to the flickr server. By calling flickr.authorizeUser() we will recieve a request token inside our requestToken() callback that we use in the next step to recieve an Intent to authorize the app. Using that intent the user will be brought to the Flickr website to sign in and and given the option to authorize the app.

```
	init {
        //You shouldnt call this everytime
        flickr.authorizeUser()
    }
    
    
    override fun requestToken(token: String) {
        //intent that can be used in the mainActivity using LiveData to
        // startActivity()
      val intent =  flickr.getFlickAuthPageIntent(token)
    }
```

 Once user Authorizes the app they will be redirected back to the app with oauth_verifier data inside the intent. This will be used to exchange for a final oauth token and the app will finally be authorized to make calls to the rest services. inside the onResume() of your main activity.
 
 ```
  override fun onResume() {
        super.onResume()
        flickr.getOauthVerifier(intent)
    }

```

This callback will get called as soon as the app has an oauth token.

 ```
	override fun authenticationSuccess(results: Boolean) {
     	       println(results)
   	 }
    
 ```
