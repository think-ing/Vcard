package com.vcard.admin.vcard;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import a_vcard.android.syncml.pim.VDataBuilder;
import a_vcard.android.syncml.pim.vcard.VCardParser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;


public class MainActivity extends Activity implements View.OnClickListener {

    private ListView listView;
    private MyListAdapter myListAdapter;
    private Button button1,button2;
    private EditText editText;
    private List<ContactInfo> contactInfoList = new ArrayList<ContactInfo>();
    private List<ContactInfo> tempContactInfoList = new ArrayList<ContactInfo>();

    private final String SHARED_PREFERENCES = "com_vcard_admin_vcard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去掉窗口标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);//标题
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏
        setContentView(R.layout.activity_main);

        button1 = (Button)findViewById(R.id.button1);
        button2 = (Button)findViewById(R.id.button2);

        listView = (ListView)findViewById(R.id.listView);
        editText = (EditText)findViewById(R.id.id_editText);

        init();
    }

    private void init() {
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);

        SharedPreferences share = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        try{
            restoreContacts(share.getString("para", ""));
        }catch(Exception e){

        }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (tempContactInfoList != null && tempContactInfoList.size() > position) {
                    ContactInfo info = tempContactInfoList.get(position);

                    if (!TextUtils.isEmpty(info.tel)) {
                        if (info.tel.indexOf(",") >= 0) {
                            //声明一个AlertDialog构造器
                            //private AlertDialog.Builder builder;
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setIcon(R.mipmap.ic_launcher);
                            builder.setTitle("提示");
                            /**
                             * 设置内容区域为单选列表项
                             */
                            final String[] items = info.tel.split(",");
                            builder.setSingleChoiceItems(items, 1, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(Intent.ACTION_DIAL);
                                    Uri data = Uri.parse("tel:" + items[i]);
                                    intent.setData(data);
                                    startActivity(intent);
                                }
                            });
                            builder.setCancelable(true);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            Uri data = Uri.parse("tel:" + info.tel);
                            intent.setData(data);
                            startActivity(intent);
                        }
                    }
                }
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                tempContactInfoList.clear();
                if (!TextUtils.isEmpty(s.toString())) {
                    for (ContactInfo info : contactInfoList) {
                        if (info.pinYinHeadChar.equals(s.toString()) || info.pinYinAll.indexOf(s.toString()) > -1 || info.name.indexOf(s.toString()) > -1) {
                            tempContactInfoList.add(info);
                        }
                    }
                } else {
                    tempContactInfoList.addAll(contactInfoList);
                }
                if (myListAdapter != null) {
                    myListAdapter.notifyDataSetChanged();
                }
            }
        });
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.id_vCard) {
//            Log.i("---mzw---", "查看vCard文件...");
//            startActivityForResult(new Intent(this, MyListFile.class), 0);
//            return true;
//        }else if (id == R.id.id_CSV) {
//            Log.i("---mzw---","新建并保存...");
//            Toast.makeText(this,"功能研发中...",Toast.LENGTH_SHORT).show();
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button1:
                Log.i("---mzw---", "查看vCard文件...");
                startActivityForResult(new Intent(this, MyListFile.class), 0);
                break;
            case R.id.button2:
                Log.i("---mzw---","新建并保存...");
                Toast.makeText(this,"功能研发中...",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * 获取vCard文件中的联系人信息
     * @return
     */
    public void restoreContacts(String filePath) throws Exception {

        if(!TextUtils.isEmpty(filePath)){
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"));
            String line;
            String name = "";
            String tel = "";
            int i = 1;
            while((line = reader.readLine()) != null) {
                if(("BEGIN:VCARD").equals(line)){
                    name = "";
                    tel = "";
                }

                if(!TextUtils.isEmpty(line) && line.startsWith("FN")){
                    name = line.replaceAll("FN:","");
                }
                if(!TextUtils.isEmpty(line) && line.indexOf("TEL") >= 0){
                    String s = line.substring(line.lastIndexOf(":") + 1,line.length()).trim();
                    if(!TextUtils.isEmpty(s) && !s.startsWith("10")){
                        tel += "," + s;
                    }
                }

                if(("END:VCARD").equals(line)){
                    contactInfoList.add(new ContactInfo(i,name, tel.replaceFirst(",","")));
                    i++;
                }
            }
            reader.close();
            Collections.sort(contactInfoList, new MyCompartor());     //按照age升序 22，23，
            tempContactInfoList.addAll(contactInfoList);

            myListAdapter = new MyListAdapter();
            listView.setAdapter(myListAdapter);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data){
        if(data != null){
            String para = data.getStringExtra("para");
            SharedPreferences share = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
            SharedPreferences.Editor edit = share.edit(); //编辑文件
            edit.putString("para", para);         //根据键值对添加数据
            edit.commit();  //保存数据信息

            switch (resultCode){
                case RESULT_OK: /* 取得数据，并显示于画面上 */
                    try{
                        restoreContacts(para);
                    }catch (Exception e){

                    }
                    break;
                default:
                    break;
            }
        }
    }

    class MyListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return tempContactInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            return tempContactInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ContactInfo info = tempContactInfoList.get(position);

            final ViewHolder holder;
            if (convertView == null) {
                convertView = MainActivity.this.getLayoutInflater().inflate(R.layout.contact_list_item, parent, false);
                holder = new ViewHolder();
                assert convertView != null;

                holder.textView = (TextView) convertView.findViewById(R.id.textView);
                holder.name_view = (TextView) convertView.findViewById(R.id.id_file_name);
                holder.info_view = (TextView) convertView.findViewById(R.id.id_file_info);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            int i = info.rank % 10;
            int resid = R.mipmap.a;
            switch (i){
                case 0:
                    resid = R.mipmap.a;
                    break;
                case 1:
                    resid = R.mipmap.b;
                    break;
                case 2:
                    resid = R.mipmap.c;
                    break;
                case 3:
                    resid = R.mipmap.d;
                    break;
                case 4:
                    resid = R.mipmap.e;
                    break;
                case 5:
                    resid = R.mipmap.f;
                    break;
                case 6:
                    resid = R.mipmap.g;
                    break;
                case 7:
                    resid = R.mipmap.a;
                    break;
                case 8:
                    resid = R.mipmap.b;
                    break;
                case 9:
                    resid = R.mipmap.c;
                    break;

            }
            holder.textView.setBackgroundResource(resid);
            holder.textView.setText(info.name.substring(0,1));
            holder.name_view.setText(info.name);
            holder.info_view.setText(info.tel);

            return convertView;
        }
    }
    class ViewHolder{
        TextView textView;
        TextView name_view;
        TextView info_view;
    }

    public static StringBuffer sb = new StringBuffer();
    class ContactInfo implements Comparable {
        int rank;
        String name;
        String tel;
        String pinYinFirstLetter;//获取汉字字符串的第一个字母
        String pinYinHeadChar;//获取汉字字符串的首字母，英文字符不变
        String pinYinAll;//获取汉字字符串的汉语拼音，英文字符不变

        public ContactInfo(int rank,String name, String tel) {
            this.rank = rank;
            this.name = name;
            this.tel = tel;
            this.pinYinAll = Cn2Spell.getPinYin(name).toLowerCase();
            this.pinYinHeadChar = Cn2Spell.getPinYinHeadChar(name).toLowerCase();
            this.pinYinFirstLetter = Cn2Spell.getPinYinFirstLetter(name).toLowerCase();
        }

        @Override
        public int compareTo(Object o) {
            ContactInfo sdto = (ContactInfo)o;
            String _pinYinFirstLetter = sdto.pinYinFirstLetter;
            return this.pinYinFirstLetter.compareTo(_pinYinFirstLetter);
        }
    }
    class MyCompartor implements Comparator{
        @Override
        public int compare(Object o1, Object o2){
            ContactInfo sdto1= (ContactInfo )o1;
            ContactInfo sdto2= (ContactInfo )o2;
            return sdto1.pinYinFirstLetter.compareTo(sdto2.pinYinFirstLetter);
        }
    }
}
