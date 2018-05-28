package com.vcard.admin.vcard;

import java.io.File;
import java.io.FileInputStream;
import java.text.Collator;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
/**
 * explain
 * Created by mzw on 2017/4/6.
 */
public class MyListFile extends ListActivity{
    /** Called when the activity is first created. */
//    private List<String> items = null;//存放名称
    private List<FileInfo> fileList = null;//存放路径
    private String rootPath = "/";
    private TextView tv;
    private Map<String,Integer> map = new HashMap<String, Integer>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_list_file);
        tv = (TextView) this.findViewById(R.id.TextView);
        rootPath = Environment.getExternalStorageDirectory()+"";

        map.put("aac", R.mipmap.file_aac);
        map.put("asf", R.mipmap.file_asf);
        map.put("avi", R.mipmap.file_avi);
        map.put("bin", R.mipmap.file_bin);
        map.put("bmp", R.mipmap.file_bmp);

        map.put("class", R.mipmap.file_class);
        map.put("cpp", R.mipmap.file_cpp);
        map.put("doc", R.mipmap.file_doc);
        map.put("gif", R.mipmap.file_gif);
        map.put("html", R.mipmap.file_html);

        map.put("ini", R.mipmap.file_ini);
        map.put("java", R.mipmap.file_java);
        map.put("jpeg", R.mipmap.file_jpeg);
        map.put("jpg", R.mipmap.file_jpg);
        map.put("js", R.mipmap.file_js);

        map.put("log", R.mipmap.file_log);
        map.put("mov", R.mipmap.file_mov);
        map.put("mp3", R.mipmap.file_mpsan);
        map.put("mp4", R.mipmap.file_mpsi);
        map.put("file", R.mipmap.file_open);

        map.put("pdf", R.mipmap.file_pdf);
        map.put("png", R.mipmap.file_png);
        map.put("ppt", R.mipmap.file_ppt);
        map.put("rar", R.mipmap.file_rar);
        map.put("rmvb", R.mipmap.file_rmvb);

        map.put("3gp", R.mipmap.file_sangp);
        map.put("tif", R.mipmap.file_tif);
        map.put("txt", R.mipmap.file_txt);
        map.put("wav", R.mipmap.file_wav);
        map.put("wma", R.mipmap.file_wma);

        map.put("wmv", R.mipmap.file_wmv);
        map.put("xls", R.mipmap.file_xls);
        map.put("xml", R.mipmap.file_xml);
        map.put("zip", R.mipmap.file_zip);
        map.put("apk", R.mipmap.file_apk);

        map.put("mkv", R.mipmap.file_mkv);
        map.put("vcf", R.mipmap.file_vcf);

        this.getFileDir(rootPath);//获取rootPath目录下的文件.
    }

    public void getFileDir(String filePath) {
        try{
            this.tv.setText("当前路径:"+filePath);// 设置当前所在路径
            fileList = new ArrayList<FileInfo>();
            File f = new File(filePath);
            File[] files = f.listFiles();// 列出所有文件
            // 如果不是根目录,则列出返回根目录和上一目录选项
            if (!filePath.equals(rootPath)) {
                fileList.add(new FileInfo("返回根目录",rootPath,-1,"",0));
                fileList.add(new FileInfo("返回上一层目录",f.getParent(),-2,"",0));
            }
            // 将所有文件存入list中
            if(files != null){
                int count = files.length;// 文件个数
                for (int i = 0; i < count; i++) {
                    File file = files[i];
                    String _fileName = file.getName();
                    String _filePath = file.getPath();
                    if(!_fileName.startsWith(".") && !_fileName.startsWith("_")){
                        String houzhui = "";
                        String info = "";
                        int rank = 0;
                        if(file.isDirectory()){
                            rank = 1;
                            houzhui = "file";
                            File[] fs = file.listFiles();// 列出所有文件
                            if(fs != null){
                                info = "包含 " + fs.length + " 个文件";
                            }else{
                                info = "空文件夹";
                            }
                        }else{
                            rank = 2;
                            houzhui = _fileName.substring(_fileName.lastIndexOf(".") + 1,_fileName.length()).toLowerCase();

                            long size = 0;
                            if (file.exists()) {
                                FileInputStream fis = null;
                                fis = new FileInputStream(file);
                                size = fis.available();
                            }
                            info = "大小 "+FormetFileSize(size);
                        }


                        if(houzhui.startsWith("ppt")){
                            houzhui = "ppt";
                        }
                        if(houzhui.startsWith("xls")){
                            houzhui = "xls";
                        }
                        if(houzhui.startsWith("htm")){
                            houzhui = "html";
                        }
                        if(houzhui.startsWith("jpeg")){
                            houzhui = "jpg";
                        }
                        if(houzhui.startsWith("doc")){
                            houzhui = "doc";
                        }

                        Integer img = map.get(houzhui);

                        if(img == null || img <= 0){
                            img = R.mipmap.file_unknow;
                        }
//                        Log.i("---mzw---","_fileName:" + _fileName + " , houzhui:" + houzhui + " , img:" + img);
                        fileList.add(new FileInfo(_fileName, _filePath,img,info,rank));
                    }
                }
            }

            Collections.sort(fileList,new MyCompartor());
//            Collections.sort(fileList, Collator.getInstance(java.util.Locale.CHINA));
            MyListAdapter adapter = new MyListAdapter();
            this.setListAdapter(adapter);
        }catch(Exception ex){
            ex.printStackTrace();
        }

    }
    class MyCompartor implements Comparator{
        @Override
        public int compare(Object o1, Object o2){
            FileInfo sdto1= (FileInfo )o1;
            FileInfo sdto2= (FileInfo )o2;
            return sdto1.compareTo(sdto2);
        }
    }


    class MyListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return fileList.size();
        }

        @Override
        public Object getItem(int position) {
            return fileList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FileInfo info = fileList.get(position);

            final ViewHolder holder;
            if (convertView == null) {
                convertView = MyListFile.this.getLayoutInflater().inflate(R.layout.file_list_item, parent, false);
                holder = new ViewHolder();
                assert convertView != null;

                holder.image_view = (ImageView) convertView.findViewById(R.id.imageView);
                holder.name_view = (TextView) convertView.findViewById(R.id.id_file_name);
                holder.info_view = (TextView) convertView.findViewById(R.id.id_file_info);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.image_view.setImageResource(info.getImage());
            holder.name_view.setText(info.getName());
            holder.info_view.setText(info.getInfo());

            return convertView;
        }
    }
    class ViewHolder{
        ImageView image_view;
        TextView name_view;
        TextView info_view;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        final FileInfo info = fileList.get(position);
        File file = new File(info.getPath());
        //如果是文件夹就继续分解
        if(file.isDirectory()){
            this.getFileDir(info.getPath());
        }else{
            final String para = info.getPath();
            if (!TextUtils.isEmpty(para)) {
                if ("vcf".equals(para.substring(para.lastIndexOf(".") + 1, para.length()).toLowerCase())) {
                    new AlertDialog.Builder(this).setTitle("提示").setMessage("选中的文件是 ： " + info.getName() + " ！").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.putExtra("para", para);
                            MyListFile.this.setResult(RESULT_OK, intent);
                            MyListFile.this.finish();
                        }
                    }).show();
                } else {
                    new AlertDialog.Builder(MyListFile.this).setTitle("抱歉").setMessage("只支持 vcf 文件 ！！").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
                }
            }


        }
    }

    public static final int SIZETYPE_B = 1;// 获取文件大小单位为B的double值
    public static final int SIZETYPE_KB = 2;// 获取文件大小单位为KB的double值
    public static final int SIZETYPE_MB = 3;// 获取文件大小单位为MB的double值
    public static final int SIZETYPE_GB = 4;// 获取文件大小单位为GB的double值

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    private static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    /**
     * 转换文件大小,指定转换的类型
     * @param fileS
     * @param sizeType
     * @return
     */
    private static double FormetFileSize(long fileS,int sizeType)
    {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        switch (sizeType) {
            case SIZETYPE_B:
                fileSizeLong=Double.valueOf(df.format((double) fileS));
                break;
            case SIZETYPE_KB:
                fileSizeLong=Double.valueOf(df.format((double) fileS / 1024));
                break;
            case SIZETYPE_MB:
                fileSizeLong=Double.valueOf(df.format((double) fileS / 1048576));
                break;
            case SIZETYPE_GB:
                fileSizeLong=Double.valueOf(df.format((double) fileS / 1073741824));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }

}
