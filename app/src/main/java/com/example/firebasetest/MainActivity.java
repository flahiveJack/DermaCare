package com.example.firebasetest;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ramijemli.percentagechartview.PercentageChartView;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    protected Interpreter tflite;
    private MappedByteBuffer tfliteModel;
    private TensorImage inputImageBuffer;
    private  int imageSizeX;
    private  int imageSizeY;
    private TensorBuffer outputProbabilityBuffer;
    private TensorProcessor probabilityProcessor;
    private static final float IMAGE_MEAN = 0.0f;
    private static final float IMAGE_STD = 1.0f;
    private static final float PROBABILITY_MEAN = 0.0f;
    private static final float PROBABILITY_STD = 255.0f;
    private Bitmap bitmap;
    private List<String> labels;

    ImageView imageView;
    Uri imageuri;
    Button run, next;

    TextView result1;

    private StorageReference storageReference;
    private FirebaseFirestore firestore;




    private FirebaseAuth auth;
    private String currentUserId;



    private BottomSheetBehavior sheetBehavior;
    private PercentageChartView mChart;
    private BarChart mBarChart;
    RelativeLayout mBottomSheetLayout;
    //Button logout = findViewById(R.id.logoutBtn);




    TextView prediction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView=(ImageView)findViewById(R.id.image);
        next=(Button)findViewById(R.id.goNext);
        run=(Button)findViewById(R.id.runbtn);
        //prediction=(TextView)findViewById(R.id.predictions);
        result1=(TextView)findViewById(R.id.resulttext);
        //mChart=findViewById(R.id.chart);

        storageReference = FirebaseStorage.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();

        String answer1 = getIntent().getStringExtra("firstAnswer");
        String answer2 = getIntent().getStringExtra("secondAnswer");
        String question1 = getIntent().getStringExtra("firstQuestion");
        String question2 = getIntent().getStringExtra("secondQuestion");




        mChart = findViewById(R.id.chart);
        mBarChart = findViewById(R.id.Barchart);

        mBottomSheetLayout = findViewById(R.id.bottom_sheet_layout);
        sheetBehavior = BottomSheetBehavior.from(mBottomSheetLayout);


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mProgressBar.setVisibility(View.VISIBLE);
                String result = result1.getText().toString();
                if (imageuri !=null){
                    StorageReference resultRef = storageReference.child("result_images").child(FieldValue.serverTimestamp().toString() + ".jpg");
                    resultRef.putFile(imageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()){
                                resultRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        HashMap<String , Object> resultMap = new HashMap<>();
                                        resultMap.put("image" , uri.toString());
                                        resultMap.put("user" , currentUserId);
                                        resultMap.put("Result" , result);
                                        resultMap.put("Question1" , question1);
                                        resultMap.put("Answer1" , answer1);
                                        resultMap.put("Question2" , question2);
                                        resultMap.put("Answer2" , answer2);
                                        resultMap.put("time" , FieldValue.serverTimestamp());

                                        firestore.collection("Results").add(resultMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                if (task.isSuccessful()){
                                                    //mProgressBar.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(MainActivity.this, "Result Added Successfully !!", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(MainActivity.this , ResultsActivity.class));
                                                    finish();
                                                }else{
                                                    //mProgressBar.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(MainActivity.this, task.getException().toString() , Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                });

                            }else{
                                //mProgressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(MainActivity.this, task.getException().getMessage() , Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    //mProgressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this, "Please Add Image and Write Your caption", Toast.LENGTH_SHORT).show();
                }
            }
        });


















        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),12);
            }
        });

        try{
            tflite=new Interpreter(loadmodelfile(MainActivity.this));
        }catch (Exception e) {
            e.printStackTrace();
        }

        run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int imageTensorIndex = 0;
                int[] imageShape = tflite.getInputTensor(imageTensorIndex).shape();
                imageSizeY = imageShape[1];
                imageSizeX = imageShape[2];
                DataType imageDataType = tflite.getInputTensor(imageTensorIndex).dataType();

                int probabilityTensorIndex = 0;
                int[] probabilityShape =
                        tflite.getOutputTensor(probabilityTensorIndex).shape();
                DataType probabilityDataType = tflite.getOutputTensor(probabilityTensorIndex).dataType();

                inputImageBuffer = new TensorImage(imageDataType);
                outputProbabilityBuffer = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType);
                probabilityProcessor = new TensorProcessor.Builder().add(getPostprocessNormalizeOp()).build();

                inputImageBuffer = loadImage(bitmap);

                tflite.run(inputImageBuffer.getBuffer(),outputProbabilityBuffer.getBuffer().rewind());
                showresult();

                //bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);


            }
        });


    }

    private TensorImage loadImage(final Bitmap bitmap) {
        // Loads bitmap into a TensorImage.
        inputImageBuffer.load(bitmap);

        // Creates processor for the TensorImage.
        int cropSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
        // TODO(b/143564309): Fuse ops inside ImageProcessor.
        ImageProcessor imageProcessor =
                new ImageProcessor.Builder()
                        .add(new ResizeWithCropOrPadOp(cropSize, cropSize))
                        .add(new ResizeOp(imageSizeX, imageSizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                        .add(getPreprocessNormalizeOp())
                        .build();
        return imageProcessor.process(inputImageBuffer);
    }

    private MappedByteBuffer loadmodelfile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor=activity.getAssets().openFd("model.tflite");
        FileInputStream inputStream=new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel=inputStream.getChannel();
        long startoffset = fileDescriptor.getStartOffset();
        long declaredLength=fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startoffset,declaredLength);
    }

    private TensorOperator getPreprocessNormalizeOp() {
        return new NormalizeOp(IMAGE_MEAN, IMAGE_STD);
    }
    private TensorOperator getPostprocessNormalizeOp(){
        return new NormalizeOp(PROBABILITY_MEAN, PROBABILITY_STD);
    }




    public static void barchart(BarChart barChart, ArrayList<BarEntry> arrayList, final ArrayList<String> xAxisValues) {
        barChart.setDrawBarShadow(false);
        barChart.setFitBars(true);
        barChart.setDrawValueAboveBar(true);
        barChart.setMaxVisibleValueCount(25);
        barChart.setPinchZoom(true);



        barChart.setDrawBorders(false);
        barChart.setDrawGridBackground(false);

        barChart.getDescription().setEnabled(true);
        //mChart.getLegend().setEnabled(false);

        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawLabels(true);
        barChart.getAxisLeft().setDrawAxisLine(false);
        barChart.getAxisLeft().setTextColor(Color.BLUE);

        barChart.getXAxis().setDrawGridLines(false);
        barChart.getXAxis().setDrawLabels(true);
        barChart.getXAxis().setDrawAxisLine(false);

        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getAxisRight().setDrawLabels(false);
        barChart.getAxisRight().setDrawAxisLine(false);
        barChart.getAxisRight().setTextColor(Color.BLUE);

        
        BarDataSet barDataSet = new BarDataSet(arrayList, "Class");
        barDataSet.setColors(new int[]{Color.parseColor("#89CFF0"), Color.parseColor("#0096FF"),
                Color.parseColor("#3F00FF"), Color.parseColor("#0F52BA"), Color.parseColor("#7DF9FF")});

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.1f);
        barData.setValueTextSize(0f);

        barChart.setBackgroundColor(Color.TRANSPARENT);
        barChart.setDrawGridBackground(false);
        barChart.animateY(2000);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);

        //Legend l = barChart.getLegend(); // Customize the ledgends
        //l.setTextSize(10f);
        //l.setFormSize(10f);
//To set components of x axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setTextSize(13f);
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.BLUE);

        barChart.setData(barData);

    }





    private void showresult(){

        try{
            labels = FileUtil.loadLabels(MainActivity.this,"labels.txt");
        }catch (Exception e){
            e.printStackTrace();
        }
        Map<String, Float> labeledProbability =
                new TensorLabel(labels, probabilityProcessor.process(outputProbabilityBuffer))
                        .getMapWithFloatValue();
        float maxValueInMap =(Collections.max(labeledProbability.values()));

        for (Map.Entry<String, Float> entry : labeledProbability.entrySet()) {
            if (entry.getValue()==maxValueInMap) {
                result1.setText(entry.getKey() + " " + maxValueInMap * 100 + "%");
                mChart.setProgress(maxValueInMap*100,true);
            }

            String[] label = labeledProbability.keySet().toArray(new String[0]);
            Float[] label_probability = labeledProbability.values().toArray(new Float[0]);







            mBarChart = findViewById(R.id.Barchart);
            mBarChart.getXAxis().setDrawGridLines(false);
            mBarChart.getAxisLeft().setDrawGridLines(false);
            // PREPARING THE ARRAY LIST OF BAR ENTRIES
            ArrayList<BarEntry> barEntries = new ArrayList<>();
            for(int i=0; i<label_probability.length; i++)
            {
                barEntries.add(new BarEntry(i, label_probability[i]*100));
            }

            // TO ADD THE VALUES IN X-AXIS
            ArrayList<String> xAxisName = new ArrayList<>();
            for(int i=0;i<label.length; i++)
            {
                xAxisName.add(label[i]);
            }
            barchart(mBarChart,barEntries,xAxisName);
            //prediction.setText("Predictions:");







        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 12 && resultCode == RESULT_OK && data != null) {
            imageuri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageuri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }




}