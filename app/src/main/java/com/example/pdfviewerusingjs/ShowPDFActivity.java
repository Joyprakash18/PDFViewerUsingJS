package com.example.pdfviewerusingjs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pdfviewerusingjs.adapter.ImageListRecyclerAdapter;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.FileRequestListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;

public class ShowPDFActivity extends AppCompatActivity {
    PhotoView mContainer;
    private ParcelFileDescriptor parcelFileDescriptor;
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page mCurrentPage;
    ArrayList<Bitmap> imageList = new ArrayList<Bitmap>();
    private PinchRecyclerView mImageListRecycler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_pdf);
        try {
            openRenderer(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mImageListRecycler = findViewById(R.id.imageListRecyclerView);
//        mContainer = findViewById(R.id.container);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        showPage(0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void openRenderer(final Context context) throws IOException {
        // In this sample, we read a PDF from the assets directory.
        FileLoader.with(this).load("http://www.pdf995.com/samples/pdf.pdf").fromDirectory("PDFFiles", FileLoader.DIR_CACHE)
                .asFile(new FileRequestListener<File>() {
                    @Override
                    public void onLoad(FileLoadRequest request, FileResponse<File> response) {
                        try {
                            File file = response.getBody();
                            parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
                            // This is the PdfRenderer we use to render the PDF.
                            if (parcelFileDescriptor != null) {
                                pdfRenderer = new PdfRenderer(parcelFileDescriptor);
                            }
                            showPage(0);
//                            setImageListAdapter();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(FileLoadRequest request, Throwable t) {
                        Toast.makeText(ShowPDFActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setImageListAdapter() {
        mImageListRecycler.setHasFixedSize(true);
        mImageListRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        ImageListRecyclerAdapter imageListRecyclerAdapter = new ImageListRecyclerAdapter(this,imageList);
        mImageListRecycler.setAdapter(imageListRecyclerAdapter);
    }

//    private void openRenderer() throws IOException {
//        // Reading a PDF file from the assets directory.
//        fileDescriptor = getAssets().openFd("compressed.tracemonkey-pldi-09.pdf").getParcelFileDescriptor();
//
//        // This is the PdfRenderer we use to render the PDF.
//        pdfRenderer = new PdfRenderer(fileDescriptor);
//    }

//    private void showPage(int index) {
//        if (pdfRenderer.getPageCount() <= index) {
//            return;
//        }
//        // Make sure to close the current page before opening another one.
//        if (null != currentPage) {
//            currentPage.close();
//        }
//        //open a specific page in PDF file
//        currentPage = pdfRenderer.openPage(index);
//        // Important: the destination bitmap must be ARGB (not RGB).
//        Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(),
//                Bitmap.Config.ARGB_8888);
//        // Here, we render the page onto the Bitmap.
//        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
//        // showing bitmap to an imageview
//        mContainer.setImageBitmap(bitmap);
//    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showPage(int index) {
        for (int i=0;i<getPageCount();i++){
            mCurrentPage = pdfRenderer.openPage(i);
            // Important: the destination bitmap must be ARGB (not RGB).
            Bitmap bitmap = Bitmap.createBitmap( getResources().getDisplayMetrics().densityDpi * mCurrentPage.getWidth() / 72,
                    getResources().getDisplayMetrics().densityDpi * mCurrentPage.getHeight() / 72,
                    Bitmap.Config.ARGB_8888);
            // Here, we render the page onto the Bitmap.
            mCurrentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            imageList.add(bitmap);
            mCurrentPage.close();
        }
        setImageListAdapter();
//        if (pdfRenderer.getPageCount() <= index) {
//            return;
//        }
//        // Make sure to close the current page before opening another one.
//        if (null != mCurrentPage) {
//            mCurrentPage.close();
//        }
//        //open a specific page in PDF file
//        mCurrentPage = pdfRenderer.openPage(index);
//        // Important: the destination bitmap must be ARGB (not RGB).
//        Bitmap bitmap = Bitmap.createBitmap( getResources().getDisplayMetrics().densityDpi * mCurrentPage.getWidth() / 72,
//                getResources().getDisplayMetrics().densityDpi * mCurrentPage.getHeight() / 72,
//                Bitmap.Config.ARGB_8888);
//        // Here, we render the page onto the Bitmap.
//        mCurrentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
//        // showing bitmap to an imageview
//        mContainer.setImageBitmap(bitmap);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public int getPageCount() {
        return pdfRenderer.getPageCount();
    }

}
