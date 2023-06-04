package com.codev.android

import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.codev.android.databinding.RecycleCommunityCommentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AdapterCommunityQnaParentCommentList(private val context: Context, private val listData: ArrayList<QnaDetailComment>, private val viewerEmail: String, private val sendParentId: (id: Int, nickname: String, type: String) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    //뷰 홀더 바인딩
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return  QnaItemViewHolder(context, RecycleCommunityCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    //뷰 홀더에 데이터 바인딩
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is QnaItemViewHolder -> {
                holder.bind(listData[position],position)
            }
        }
    }

    //뷰 홀더 데이터 개수
    override fun getItemCount(): Int = listData.size

    //Item의 ViewHolder 객체
    inner class QnaItemViewHolder(val context: Context, private val binding: RecycleCommunityCommentBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: QnaDetailComment, position: Int){
            val adapter = AdapterCommunityQnaChildCommentList(context, data.coReCommentOfQnaBoardList, viewerEmail) {
                sendParentId(it, "none", "child delete")
            }
            binding.rvComment.adapter = adapter
            binding.nickname.text = data.co_nickname
            binding.content.text = data.content
            binding.date.text = stringToTime(data.createdAt.toString())
            binding.btnMore.setOnClickListener {
                confirmDelete(itemView.context, data.co_coqb)
            }
            Glide.with(itemView.context)
                .load(data.profileImg).circleCrop()
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        CoroutineScope(Dispatchers.Main).launch {
                            Glide.with(itemView.context)
                                .load("http://semtle.catholic.ac.kr:8080/image?name=Profile_Basic20230130012110.png")
                                .circleCrop()
                                .into(binding.profileImg)
                        }
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                })
                .into(binding.profileImg)

                binding.btnComment.setOnClickListener {
                    sendParentId(data.co_coqb, data.co_nickname, "none")
                }

            binding.btnMore.setOnClickListener {
                sendParentId(data.co_coqb, data.co_nickname, "parent delete")
            }
        }

        private fun confirmDelete(context: Context, id: Int){
            // 다이얼로그를 생성하기 위해 Builder 클래스 생성자를 이용해 줍니다.
            val builder = AlertDialog.Builder(context)
            builder.setTitle("댓글 삭제")
                .setMessage("해당 댓글을 정말로 삭제하시겠습니까?")
                .setPositiveButton("확인",
                    DialogInterface.OnClickListener { dialog, _ ->
                        binding.btnMore.isClickable = false
                        RetrofitClient.service.deleteQnaParentComment(AndroidKeyStoreUtil.decrypt(UserSharedPreferences.getUserAccessToken()), id).enqueue(object:
                            Callback<ResConfirm> {
                            override fun onResponse(call: Call<ResConfirm>, response: Response<ResConfirm>) {
                                if(response.isSuccessful.not()){
                                    Log.d("test: 포트폴리오 삭제 실패",response.toString())
                                    Toast.makeText(context, "서버와 연결을 시도했으나 실패했습니다.", Toast.LENGTH_SHORT).show()
                                }
                                when(response.code()){
                                    200 -> {
                                        response.body()?.let {
                                            notifyItemRemoved(adapterPosition)
                                        }
                                    }
                                }
                            }
                            override fun onFailure(call: Call<ResConfirm>, t: Throwable) {
                                Log.d("test", "[Fail]${t.toString()}")
                                binding.btnMore.isClickable = true
                            }
                        })
                    })
                .setNegativeButton("취소",
                    DialogInterface.OnClickListener { dialog, _ ->
                        Toast.makeText(context, "취소함", Toast.LENGTH_SHORT).show()
                    })
            // 다이얼로그를 띄워주기
            builder.show()
        }
    }

    fun setViewMode(boolean: Boolean){
        if (boolean){
            //뷰어와 댓글 작성자 같을 때 : 작성자
            Log.d("test","작성자 모드")
        }else{
            //뷰어와 댓글 작성자 다를 때 : 뷰어
            Log.d("test","뷰어 모드")
        }
    }

    private fun stringToTime(string: String) : String{
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")
        val convertTime = LocalDateTime.parse(string, formatter)
        return convertTime.format(DateTimeFormatter.ofPattern("MM/dd HH:mm"))
    }



}