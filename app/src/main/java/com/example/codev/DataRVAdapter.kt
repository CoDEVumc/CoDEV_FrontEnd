//package com.example.codev
//
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.example.codev.databinding.ProjectlistItemBinding
//import kotlinx.android.synthetic.main.studylist_item.view.*
//import kotlinx.android.synthetic.main.projectlist_item.view.*
//
////import com.example.codev.databinding.ItemDataBinding
//
//class DataRVAdapter( val dataList: ArrayList<ProjectData> ): RecyclerView.Adapter<DataRVAdapter.DataViewHolder>() {
//
//    //viewHolder 객체
//    inner class DataViewHolder(private val viewBinding: ProjectlistItemBinding): RecyclerView.ViewHolder(viewBinding.root){
//        fun bind(data: ResRecruitP){ //이렇게 해야 viewBinding의 데이터 값을 가져올 수 있음
//            //프로젝트 제목
//            viewBinding.ptitle.text = data.co_title
//            //프로젝트 Dday <-- 계산 해야됨
//            viewBinding.pdday.text = data.co_deadLine
//            //프로젝트 총 인원
//            viewBinding.pNum.text = data.co_totalnum
//            //프로젝트 파트 list
//            viewBinding.ppartlist.text = data.co_parts
//
//            //프로젝트 이미지 ( ,로 슬라이싱 해서 하나씩 넣기 )
//            var imageList = data.co_languageList
//            viewBinding.pStackImg1.setImageIcon(imageList[0])
//            //이이이이ㅣ이이ㅣ이이이잉ㅇ읻이이이이이이이이잉ㅇ이잉
//
//
//            //프로젝트 하트여부
//            fun ishearted(){
//                if (!data.co_heart){ //co_heart == 1 : 하트 눌려있으면
//                    viewBinding.pheart.setBackgroundColor(900) //색 적용 나중에 바꿔야됨
//                }
//                //하트 안눌려있으면 (이미 설정해둔 백컬러 잇어서 코드 추가 안해도 될듯)
//                //viewBinding.pheart.setBackgroundColor(0)//색 적용 나중에 바꿔야됨
//            }
//
//        }
//        init{ //item 누르면 상세 페이지로 이동 <-- 나중에 상세 페이지 짤 때 활용!!
//            viewBinding.projectItem.setOnClickListener(View.OnClickListener {
//                val position = adapterPosition
//                Log.d("클릭","$position: 클릭했습니다. ")
//                dataList.removeAt(position)
//                notifyItemRemoved(position)
//
//                return@OnClickListener //여기에 true 입력 해야 되는거 같은디
//            })
//        }
//    }
//
//    //ViewHolder 만들어질 때 실행할 동작
//    //MainActivity에서 하던 viewBinding이랑 똑같은 작업
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
//        var viewBinding = ProjectlistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return DataViewHolder(viewBinding)
//    }
//
//    //ViewHolder가 실제로 데이터를 표시해야할 때 호출되어야 하는 함수
//    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
//        holder.bind(dataList[position])
//    }
//
//    //표현할 Item의 총 갯수
//    override fun getItemCount(): Int = dataList.size
//
//    //ClickListener
//    interface OnItemClickListener {
//        fun onClick(v: View, position: Int)
//    }
//    private lateinit var itemClickListener : OnItemClickListener
//
//    fun setItemClickListener(itemClickListener: OnItemClickListener) {
//        this.itemClickListener = itemClickListener
//    }
//}
//
////Data 객체 생성 --> Data 표시할 xml 만들고 --> DataRVAdapter 생성 --> viewHolder 객체 만들고
////ㄴProjectData --> projectList.xml --> DataRVAdapter -->