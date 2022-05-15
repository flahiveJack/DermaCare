package com.example.firebasetest;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder>{

    private List<Post> mList;
    private List<Users> usersList;
    private Activity context;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    public PostAdapter(Activity context , List<Post> mList , List<Users> usersList){
        this.mList = mList;
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.each_post , parent , false);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        return new PostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = mList.get(position);
        holder.setPostPic(post.getImage());
        holder.setPostCaption(post.getCaption());
        holder.setPostAddress(post.getAddress());
        holder.setPostPhone(post.getPhone());
        holder.setPostWebsite(post.getWebsite());

        long milliseconds = post.getTime().getTime();
        String date  = DateFormat.format("MM/dd/yyyy" , new Date(milliseconds)).toString();
        holder.setPostDate(date);

        String username = usersList.get(position).getName();
        String image = usersList.get(position).getImage();

        holder.setProfilePic(image);
        holder.setPostUsername(username);



        String postId = post.PostId;
        String currentUserId = auth.getCurrentUser().getUid();


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder{
        ImageView postPic;
        CircleImageView profilePic ;
        TextView postUsername , postDate , postCaption , postAddress, postPhone, postWebsite;

        View mView;
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;


        }

        public void setPostPic(String urlPost){
            postPic = mView.findViewById(R.id.user_post);
            Glide.with(context).load(urlPost).into(postPic);
        }
        public void setProfilePic(String urlProfile){
            profilePic = mView.findViewById(R.id.profile_pic);
            Glide.with(context).load(urlProfile).into(profilePic);
        }
        public void setPostUsername(String username){
            postUsername = mView.findViewById(R.id.username_tv);
            postUsername.setText(username);
        }
        public void setPostDate(String date){
            postDate = mView.findViewById(R.id.date_tv);
            postDate.setText(date);
        }
        public void setPostCaption(String caption){
            postCaption = mView.findViewById(R.id.caption_tv);
            postCaption.setText(caption);
        }
        public void setPostAddress(String address){
            postAddress = mView.findViewById(R.id.address_tv);
            postAddress.setText(address);
        }
        public void setPostPhone(String phone){
            postPhone = mView.findViewById(R.id.phone_tv);
            postPhone.setText(phone);
        }
        public void setPostWebsite(String website){
            postWebsite = mView.findViewById(R.id.website_tv);
            postWebsite.setText(website);
        }
    }
}
