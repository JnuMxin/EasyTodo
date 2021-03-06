package xyz.wendyltanpcy.easytodo.TodoList;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import xyz.wendyltanpcy.easytodo.Adapter.EventsAdapter;
import xyz.wendyltanpcy.easytodo.Fragment.EventContentFragment;
import xyz.wendyltanpcy.easytodo.Fragment.PickDateFragment;
import xyz.wendyltanpcy.easytodo.Fragment.PickTimeFragment;
import xyz.wendyltanpcy.easytodo.R;
import xyz.wendyltanpcy.easytodo.helper.ColorManager;
import xyz.wendyltanpcy.easytodo.helper.PictureHandle;
import xyz.wendyltanpcy.easytodo.helper.PictureUtils;
import xyz.wendyltanpcy.easytodo.model.ThemeColor;
import xyz.wendyltanpcy.easytodo.model.TodoEvent;

public class EventContentActivity extends AppCompatActivity {

    private FloatingActionButton saveDetailButton;
    private ImageView chooseDate;
    private ImageView chooseAlarm;
    private TextView eventNameText ;
    private TextView eventDetailText ;
    private TextView eventDeadLineText ;
    private static TodoEvent Event;
    private ImageView eventImage;
    private EventsAdapter.ViewHolder holder;
    private static boolean actionStartFlag = false;
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";


    //deal with pics
    private static final int SUCCESSCODE = 111;
    private String mPublicPhotoPath;
    private static final int REQ_GALLERY = 333;
    private static final int REQUEST_CODE_PICK_IMAGE = 222;
    private xyz.wendyltanpcy.easytodo.Fragment.EventContentFragment EventContentFragment;


    private static final String TAG = "EventContentActivity";

    public static void actionStart(Context context, TodoEvent event){
        Intent intent = new Intent(context,EventContentActivity.class);
        Event = event;
        actionStartFlag = true;
        context.startActivity(intent);
    }

    /*
        默认的构造器
   */
    public EventContentActivity(){

    }

    /*
    构造的时候传入适配器
     */
    public EventContentActivity(EventsAdapter.ViewHolder hd){
        holder = hd;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_content);


        //launch by notification
        Intent i = getIntent();
        if (!actionStartFlag)
            Event = (TodoEvent) i.getSerializableExtra("event");

        Toolbar toolbar =  findViewById(R.id.toolbar);
        saveDetailButton = findViewById(R.id.save_detail_button);

        setSupportActionBar(toolbar);
        getThemeColor(toolbar);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        getThemeColorForCollapse(collapsingToolbar);
        if(getSupportActionBar() != null){
            // Enable the Up button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbar.setTitle(Event.getEventName());

        EventContentFragment = (EventContentFragment)
                getSupportFragmentManager().findFragmentById(R.id.news_content_fragment);
        EventContentFragment.refresh(Event);


        chooseDate = (ImageView) findViewById(R.id.choose_date);
        chooseAlarm = (ImageView) findViewById(R.id.choose_alarm);
        eventNameText = (TextView) findViewById(R.id.event_name);
        eventDetailText = (TextView) findViewById(R.id.event_detail);
        eventDeadLineText = (TextView) findViewById(R.id.event_deadline);
        eventImage = (ImageView) findViewById(R.id.event_content_image);





        chooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = EventContentFragment.getFragmentManager();
                PickDateFragment dialog = PickDateFragment.newInstance(Event.getEventDeadline());
                dialog.setTargetFragment(EventContentFragment,REQUEST_DATE);
                dialog.show(manager,DIALOG_DATE);
            }
        });

        eventDeadLineText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = EventContentFragment.getFragmentManager();
                PickDateFragment dialog = PickDateFragment.newInstance(Event.getEventDeadline());
                dialog.setTargetFragment(EventContentFragment,REQUEST_DATE);
                dialog.show(manager,DIALOG_DATE);
            }
        });


        chooseAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = EventContentFragment.getFragmentManager();
                PickTimeFragment dialog = PickTimeFragment.newInstance(Event.getEventDeadline());
                dialog.setTargetFragment(EventContentFragment, REQUEST_TIME);
                dialog.show(manager, DIALOG_TIME);
            }
        });

        saveDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mission = eventNameText.getText().toString();
                if (!mission.isEmpty()) {
                    Event.setEventName(mission);
                    Event.setEventDetail(eventDetailText.getText().toString());
                    Event.save();
                    finish();
                } else {
                    Toast.makeText(getBaseContext(), "Mission不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /**
     * 初始化主题颜色
     */
    private void getThemeColorForCollapse(CollapsingToolbarLayout collapsingToolbar) {
        ThemeColor color = DataSupport.find(ThemeColor.class,1);
        if (color!=null) {
            collapsingToolbar.setBackgroundColor(color.getColor());
//            collapsingToolbar.setContentScrimColor(color.getColor());
        }
        else{
            color = new ThemeColor();
            color.setColor(ColorManager.DEFAULT_COLOR);
            color.save();
            collapsingToolbar.setBackgroundColor(color.getColor());
//            collapsingToolbar.setContentScrimColor(color.getColor());
        }
    }



    /**
     *  获得主题的颜色
     * @param toolbar
     */
    private void getThemeColor(Toolbar toolbar){
        ThemeColor color = DataSupport.find(ThemeColor.class,1);
        if (color != null) {
            toolbar.setBackgroundColor(0);  // 设置toolbar为透明的颜色， 不遮挡图片
            saveDetailButton.setBackgroundTintList(ColorStateList.valueOf(color.getColor()));
        }
        else{
            color = new ThemeColor();
            color.setColor(ColorManager.DEFAULT_COLOR);
            color.save();
            toolbar.setBackgroundColor(0);  // 设置toolbar为透明的颜色， 不遮挡图片
            saveDetailButton.setBackgroundTintList(ColorStateList.valueOf(color.getColor()));
        }
    }

    /*
    拍照的功能
     */
    private void showTakePicture() {
        PermissionGen.with(EventContentActivity.this)
                .addRequestCode(SUCCESSCODE)
                .permissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .request();
    }

    /*
    使用PermissionGen处理权限
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    //权限申请成功
    @PermissionSuccess(requestCode = SUCCESSCODE)
    public void doSomething() {
        //申请成功
        startTake();
    }

    @PermissionFail(requestCode = SUCCESSCODE)
    public void doFailSomething() {
        Toast.makeText(EventContentActivity.this,"Ask for permission failed!",Toast.LENGTH_SHORT).show();
    }

    /*
    将位图转换成字节数组
     */
    private byte[]img(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    private void startTake() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //判断是否有相机应用
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            //创建临时图片文件
            File photoFile = null;
            try {
                photoFile = PictureUtils.createPublicImageFile();
                mPublicPhotoPath = photoFile.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //设置Action为拍照
            if (photoFile != null) {
                takePictureIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                //这里加入flag
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri photoURI = FileProvider.getUriForFile(this, "applicationId.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQ_GALLERY);
            }
        }
    }

    private Uri uri;
    String path;
    int mTargetW;
    int mTargetH;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mTargetW = eventImage.getWidth();
        mTargetH = eventImage.getHeight();

        switch (requestCode) {
            //拍照
            case REQ_GALLERY:
                if (resultCode != Activity.RESULT_OK) return;
                uri = Uri.parse(mPublicPhotoPath);
                path = uri.getPath();
                //save pic file path
                Event.setEventPhotoPath(path);
                PictureUtils.galleryAddPic(mPublicPhotoPath, this);
                break;
            //获取相册的图片
            case REQUEST_CODE_PICK_IMAGE:
                if (data == null) return;
                uri = data.getData();
                int sdkVersion = Integer.valueOf(Build.VERSION.SDK);
                if (sdkVersion >= 19) {
                    path = this.uri.getPath();
                    path = PictureUtils.getPath_above19(EventContentActivity.this, this.uri);
                } else {
                    path = PictureUtils.getFilePath_below19(EventContentActivity.this, this.uri);
                }
                break;
        }
        Bitmap imageBitmap = PictureUtils.getSmallBitmap(path, mTargetW, mTargetH);
        eventImage.setImageBitmap(imageBitmap);
        byte[] images = img(imageBitmap);
        Event.setEventImageBitMap(images);
        Event.save();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_content, menu);
        return true;
    }

    /**
     * 点击软键盘外取消软键盘
     * @param ev
     * @return
     */

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                //使EditText触发一次失去焦点事件
                v.setFocusable(false);
//                v.setFocusable(true); //这里不需要是因为下面一句代码会同时实现这个功能
                v.setFocusableInTouchMode(true);
                return true;
            }
        }
        return false;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.captureImage) {
//           Toast.makeText(this,"capturing!",Toast.LENGTH_SHORT).show();
            showTakePicture();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
          /*
        如果事件具有图片的字节属性，就设置事件图片
         */
        super.onResume();
        //显示图片并增加点击监听
        if (Event.getEventImageBitMap() != null) {
            byte[] images = Event.getEventImageBitMap();
            Bitmap bitmap = BitmapFactory.decodeByteArray(images, 0, images.length);
            eventImage.setImageBitmap(bitmap);
            eventImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PictureHandle.actionStart(EventContentActivity.this, Event.getEventPhotoPath());
                }
            });
        }
    }
}
