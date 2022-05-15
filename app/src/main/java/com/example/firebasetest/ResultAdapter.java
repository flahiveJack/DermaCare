package com.example.firebasetest;

import android.app.Activity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder>{

    private List<Result> mList;
    private List<Users> usersList;
    private Activity context;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    public ResultAdapter(Activity context , List<Result> mList , List<Users> usersList){
        this.mList = mList;
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public ResultAdapter.ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.each_result , parent , false);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        return new ResultViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultAdapter.ResultViewHolder holder, int position) {
        Result result = mList.get(position);
        holder.setResultPic(result.getImage());
        holder.setResultResult(result.getResult());
        holder.setResultQuestion1(result.getQuestion1());
        holder.setResultAnswer1(result.getAnswer1());
        holder.setResultQuestion2(result.getQuestion2());
        holder.setResultAnswer2(result.getAnswer2());

        long milliseconds = result.getTime().getTime();
        String date  = DateFormat.format("MM/dd/yyyy" , new Date(milliseconds)).toString();
        holder.setResultDate(date);

        String username = usersList.get(position).getName();
        String image = usersList.get(position).getImage();

        holder.setProfilePic(image);
        holder.setResultUsername(username);



        String resultId = result.ResultId;
        String currentUserId = auth.getCurrentUser().getUid();


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ResultViewHolder extends RecyclerView.ViewHolder{
        ImageView resultPic;
        CircleImageView profilePic ;
        TextView resultUsername , resultDate , resultResult , resultQuestion1, resultQuestion2, resultAnswer1, resultAnswer2;

        View mView;
        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;


        }

        public void setResultPic(String urlResult){
            resultPic = mView.findViewById(R.id.user_result);
            Glide.with(context).load(urlResult).into(resultPic);
        }
        public void setProfilePic(String urlProfile){
            profilePic = mView.findViewById(R.id.profile_pic);
            Glide.with(context).load(urlProfile).into(profilePic);
        }
        public void setResultUsername(String username){
            resultUsername = mView.findViewById(R.id.username_tv);
            resultUsername.setText(username);
        }
        public void setResultDate(String date){
            resultDate = mView.findViewById(R.id.date_tv);
            resultDate.setText(date);
        }
        public void setResultResult(String result){
            resultResult = mView.findViewById(R.id.result_tv);
            resultResult.setText(result);
        }
        public void setResultQuestion1(String question1){
            resultQuestion1 = mView.findViewById(R.id.question1_tv);
            resultQuestion1.setText(question1);
        }
        public void setResultAnswer1(String answer1){
            resultAnswer1 = mView.findViewById(R.id.answer1_tv);
            resultAnswer1.setText(answer1);
        }
        public void setResultQuestion2(String question2){
            resultQuestion2 = mView.findViewById(R.id.question2_tv);
            resultQuestion2.setText(question2);
        }
        public void setResultAnswer2(String answer2){
            resultAnswer2 = mView.findViewById(R.id.answer2_tv);
            resultAnswer2.setText(answer2);
        }

    }
}
