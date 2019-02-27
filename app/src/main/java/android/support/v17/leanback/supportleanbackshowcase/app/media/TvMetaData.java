package android.support.v17.leanback.supportleanbackshowcase.app.media;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class TvMetaData implements Parcelable {
    private Uri mMediaSourceUri = null;
    private int mSourceCount;
    private String[] mMediaSourcePath;
    private String mMediaTitle;
    private String mMediaArtistName;
    private String mMediaAlbumName;
    private int mMediaAlbumArtResId;
    private String mMediaAlbumArtUrl;

    TvMetaData(Uri mediaSourceUri, String[] mediaSourcePath, String mediaTitle,
               String mediaArtistName, String mediaAlbumName, int mediaAlbumArtResId,
               String mediaAlbumArtUrl) {
        mMediaSourceUri = mediaSourceUri;
        mMediaSourcePath = mediaSourcePath;
        mSourceCount = mMediaSourcePath.length;
        mMediaTitle = mediaTitle;
        mMediaArtistName = mediaArtistName;
        mMediaAlbumName = mediaAlbumName;
        mMediaAlbumArtResId = mediaAlbumArtResId;
        mMediaAlbumArtUrl = mediaAlbumArtUrl;
    }

    public TvMetaData() {
    }

    public TvMetaData(Parcel in) {
        mMediaSourceUri = in.readParcelable(null);
        mSourceCount = in.readInt();
        mMediaSourcePath = new String[mSourceCount];
        in.readStringArray(mMediaSourcePath);
        mMediaTitle = in.readString();
        mMediaArtistName = in.readString();
        mMediaAlbumName = in.readString();
        mMediaAlbumArtResId = in.readInt();
        mMediaAlbumArtUrl = in.readString();
    }


    public Uri getMediaSourceUri() {
        return mMediaSourceUri;
    }

    public void setMediaSourceUri(Uri mediaSourceUri) {
        mMediaSourceUri = mediaSourceUri;
    }

    public String[] getMediaSourcePath() {
        return mMediaSourcePath;
    }

    public void setMediaSourcePath(String[] mediaSourcePath) {
        mMediaSourcePath = mediaSourcePath;
        mSourceCount = mMediaSourcePath.length;
    }

    public String getMediaTitle() {
        return mMediaTitle;
    }

    public void setMediaTitle(String mediaTitle) {
        mMediaTitle = mediaTitle;
    }

    public String getMediaArtistName() {
        return mMediaArtistName;
    }

    public void setMediaArtistName(String mediaArtistName) {
        mMediaArtistName = mediaArtistName;
    }

    public String getMediaAlbumName() {
        return mMediaAlbumName;
    }

    public void setMediaAlbumName(String mediaAlbumName) {
        mMediaAlbumName = mediaAlbumName;
    }


    public int getMediaAlbumArtResId() {
        return mMediaAlbumArtResId;
    }

    public void setMediaAlbumArtResId(int mediaAlbumArtResId) {
        mMediaAlbumArtResId = mediaAlbumArtResId;
    }

    public String getMediaAlbumArtUrl() {
        return mMediaAlbumArtUrl;
    }

    public void setMediaAlbumArtUrl(String mediaAlbumArtUrl) {
        mMediaAlbumArtUrl = mediaAlbumArtUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mMediaSourceUri, flags);
        dest.writeInt(mSourceCount);
        dest.writeStringArray(mMediaSourcePath);
        dest.writeString(mMediaTitle);
        dest.writeString(mMediaArtistName);
        dest.writeString(mMediaAlbumName);
        dest.writeInt(mMediaAlbumArtResId);
        dest.writeString(mMediaAlbumArtUrl);
    }

    public static final Parcelable.Creator<TvMetaData> CREATOR = new Creator<TvMetaData>() {
        @Override
        public TvMetaData createFromParcel(Parcel parcel) {
            return new TvMetaData(parcel);
        }

        @Override
        public TvMetaData[] newArray(int size) {
            return new TvMetaData[size];
        }
    };

}
