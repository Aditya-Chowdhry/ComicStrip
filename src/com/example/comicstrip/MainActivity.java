package com.example.comicstrip;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.media.effect.EffectFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
public class MainActivity extends Activity {
	private Uri mImageCaptureUri;
	private ImageView mImageView;
	
	private static final int PICK_FROM_CAMERA = 1;
	private static final int CROP_FROM_CAMERA = 2;
	private static final int PICK_FROM_FILE = 3;
	
	private static Uri fileUri;
	private static int counter=1;
	static File mediaFile;
	private static  Uri getOutputMediaFileUri(int type){
		File f = null;
	    try
	    {
		f=getOutputMediaFile(type);
	    }
	    catch(NullPointerException ex)
	    {
	    	Log.e("main","aa");
		}
		return Uri.fromFile(f);
	}
	private static  File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "ComicStrip");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("ComicStrip", "failed to create directory");
	            return null;
	        }
	    }

		// Create a media file name
	    
	    mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "Comic_"+counter+ ".jpg");
	     counter++;
		    

	    return mediaFile;
	}
	public Bitmap createSketchEffect(Bitmap source)
	{
		/*
		 * *s = Read-File-Into-Image("/path/to/image")
*g = Convert-To-Gray-Scale(s)
*i = Invert-Colors(g)
*b = Apply-Gaussian-Blur(i)
*result = Color-Dodge-Blend-Merge(b,g)
		 */
		Bitmap result = null,gray,invert,gaussian;
		gray=new grayScale().convertToGrayscale(source);
		invert=new invertColor().invert(gray);
		gaussian=new GaussianBlur().applyGaussianBlur(invert);
		result=new ColorDodgeBlendMerge().ColorDodgeBlend(gaussian, gray);
		result=new GaussianBlur().sharpen(result,result.getDensity());
		return result; 
	}
public void shareImageWhatsApp() {

    Bitmap adv = screenShot(findViewById(R.id.mainview));
    Intent share = new Intent(Intent.ACTION_SEND);
    share.setType("image/jpeg");
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    adv.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
    File f = new File(Environment.getExternalStorageDirectory()+ File.separator + "comic"+counter+".jpg");
    try {
        f.createNewFile();        
        new FileOutputStream(f).write(bytes.toByteArray());
    } catch (IOException e) {
        e.printStackTrace();
    }
    share.putExtra(Intent.EXTRA_STREAM,Uri.parse( Environment.getExternalStorageDirectory()+ File.separator+"comic"+counter+".jpg"));
    if(isPackageInstalled("com.whatsapp",this)){
          share.setPackage("com.whatsapp"); 
          startActivity(Intent.createChooser(share, "Share Image"));

    }else{

        Toast.makeText(getApplicationContext(), "Please Install Whatsapp", Toast.LENGTH_LONG).show();
    }

}

private boolean isPackageInstalled(String packagename, Context context) {
    PackageManager pm = context.getPackageManager();
    try {
        pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
        return true;
    } catch (NameNotFoundException e) {
        return false;
    }
}
	
	public static final int MEDIA_TYPE_IMAGE = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        
		
        final String [] items			= new String [] {"Take from camera", "Select from gallery"};				
		ArrayAdapter<String> adapter	= new ArrayAdapter<String> (this, android.R.layout.select_dialog_item,items);
		AlertDialog.Builder builder		= new AlertDialog.Builder(this);
		
		builder.setTitle("Select Image");
		builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
			public void onClick( DialogInterface dialog, int item ) { //pick from camera
				if (item == 0) {
					Intent intent 	 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					
					mImageCaptureUri =getOutputMediaFileUri(MEDIA_TYPE_IMAGE); 

					intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

					try {
						intent.putExtra("return-data", true);
						
						startActivityForResult(intent, PICK_FROM_CAMERA);
					} catch (ActivityNotFoundException e) {
						e.printStackTrace();
					}
				} else { //pick from file
					Intent intent = new Intent();
					
	                intent.setType("image/*");
	                intent.setAction(Intent.ACTION_GET_CONTENT);
	                
	                startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
				}
			}
		} );
		
		final AlertDialog dialog = builder.create();
		
		Button button 	= (Button) findViewById(R.id.btn_crop);
		mImageView		= (ImageView) findViewById(R.id.iv_photo);
		
		mImageView.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                int eid = event.getAction();
                switch (eid) {
                case MotionEvent.ACTION_MOVE:

                    RelativeLayout.LayoutParams mParams = (RelativeLayout.LayoutParams) mImageView.getLayoutParams();
                    int x = (int) event.getRawX();
                    int y = (int) event.getRawY();
                    mParams.leftMargin = x;
                    mParams.topMargin = y;
                     
                    mImageView.setLayoutParams(mParams);


                    break;

                default:
                    break;
                }
                return true;
            }
        });
		button.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				dialog.show();
			}
		});
		
		
    }
    public Bitmap screenShot(View view) {
	    Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
	            view.getHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(bitmap);
	    view.draw(canvas);
	    return bitmap;
	}
    public void Share(View v)
    {
    	new WhatsappDialogFragment().show(getFragmentManager(),"");
    }
    public class WhatsappDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Do you want to Share this comic ?")
                   .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                           shareImageWhatsApp();
                       }
                   })
                   .setNegativeButton("No", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                           // User cancelled the dialog
                       }
                   });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode != RESULT_OK) return;
	   
	    switch (requestCode) {
		    case PICK_FROM_CAMERA:
		    	doCrop();
		    	
		    	break;
		    	
		    case PICK_FROM_FILE: 
		    	mImageCaptureUri = data.getData();
		    	
		    	doCrop();
	    
		    	break;	    	
	    
		    case CROP_FROM_CAMERA:	    	
		        Bundle extras = data.getExtras();
	
		        if (extras != null) {	        	
		            Bitmap photo = extras.getParcelable("data");
		            Log.d("adi","adi");
		            photo=createSketchEffect(photo);
		            mImageView.setImageBitmap(photo);
		        }
	
		        
		        break;

	    }
	}
    
    private void doCrop() {
		final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();
    	
    	Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        
        List<ResolveInfo> list = getPackageManager().queryIntentActivities( intent, 0 );
        
        int size = list.size();
        
        if (size == 0) {	        
        	Toast.makeText(this, "Can not find image crop app", Toast.LENGTH_SHORT).show();
        	
            return;
        } else {
        	intent.setData(mImageCaptureUri);
            
            intent.putExtra("outputX", 100);
            intent.putExtra("outputY", 100);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);
            
        	if (size == 1) {
        		Intent i 		= new Intent(intent);
	        	ResolveInfo res	= list.get(0);
	        	
	        	i.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
	        	
	        	startActivityForResult(i, CROP_FROM_CAMERA);
        	} else {
		        for (ResolveInfo res : list) {
		        	final CropOption co = new CropOption();
		        	
		        	co.title 	= getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
		        	co.icon		= getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
		        	co.appIntent= new Intent(intent);
		        	
		        	co.appIntent.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
		        	
		            cropOptions.add(co);
		        }
	        
		        CropOptionAdapter adapter = new CropOptionAdapter(getApplicationContext(), cropOptions);
		        
		        AlertDialog.Builder builder = new AlertDialog.Builder(this);
		        builder.setTitle("Choose Crop App");
		        builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
		            public void onClick( DialogInterface dialog, int item ) {
		                startActivityForResult( cropOptions.get(item).appIntent, CROP_FROM_CAMERA);
		            }
		        });
	        
		        builder.setOnCancelListener( new DialogInterface.OnCancelListener() {
		            @Override
		            public void onCancel( DialogInterface dialog ) {
		               
		                if (mImageCaptureUri != null ) {
		                    getContentResolver().delete(mImageCaptureUri, null, null );
		                    mImageCaptureUri = null;
		                }
		            }
		        } );
		        
		        AlertDialog alert = builder.create();
		        
		        alert.show();
        	}
        }
	}
}