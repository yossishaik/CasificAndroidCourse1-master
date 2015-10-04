package app.Objects;

import android.net.Uri;

public class Image {

    public Uri mThumbUri;
    public Uri mFullUri;
    public int mOrientation;
    public Uri mExternalContentUri;

    public Image(Uri uri, int orientation){
        mThumbUri = uri;
        mOrientation = orientation;
    }

	public Image(Uri thumbUri, Uri fullUri,int orientation, Uri externalContentUri) {
		
		mThumbUri = thumbUri;
        mFullUri = fullUri;
        mOrientation = orientation;
        mExternalContentUri = externalContentUri;
	}

}
