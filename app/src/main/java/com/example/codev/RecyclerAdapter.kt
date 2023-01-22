//package com.example.codev
//
//import android.content.Context
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//
//import com.example.codev.databinding.FragmentRecruitProjectBinding
//
//
//class RecyclerAdapter(private val resultList: List<PData>, val context: Context)
//    :RecyclerView.Adapter<RecyclerAdapter.ViewHolder>(){
//    //뷰 홀더 바인딩
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        return  ProjectItemViewHolder(FragmentRecruitProjectBinding.inflate(LayoutInflater.from(parent.context), parent, false))
//    }
//
//    //뷰 홀더에 데이터 바인딩
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        when(holder){
//            is ProjectItemViewHolder -> {
//                resultList
//                holder.bind(resultList[position],position)
//            }
//        }
//    }
//
//    //뷰 홀더 데이터 개수
//    override fun getItemCount(): Int = resultList.size
//
//    //Item의 ViewHolder 객체
//    inner class ProjectItemViewHolder(private val binding: FragmentRecruitProjectBinding):
//        RecyclerView.ViewHolder(binding.root){
//        fun bind(data: PData, position: Int){
//            binding.ptitle.text = data.co_title
//            binding.pdday.text = data.co_deadLine
//            binding.pnum.text = data.co_totalnum
//
//            binding.ppartlist.text = data.co_parts
//            binding.imagelist.text = data.co_languages
//
//
//            if(data.co_heart) { //= 1
//                //binding.pheart. //pheart 가 눌려있게
//            }
//
//            binding.projectlist_item.setOnClickListener {
//                Log.d("test", "프로젝트 클릭 $position")
//            }
//
//
//        }
//    }
//
//
//    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!){
//        val ptitle = itemView?.findViewById<TextView>(R.id.ptitle)
//        val pdday = itemView?.findViewById<TextView>(R.id.pdday)
//        val pnum = itemView?.findViewById<TextView>(R.id.pNum)
//        val ppart = itemView?.findViewById<TextView>(R.id.ppartlist)
//
//        val plangs = itemView?.findViewById<LinearLayout>(R.id.imagelist)
//        val p1 = itemView?.findViewById<ImageView>(R.id.p_stack_img1)
//        val p2 = itemView?.findViewById<ImageView>(R.id.p_stack_img2)
//        val p3 = itemView?.findViewById<ImageView>(R.id.p_stack_img3)
//        val p4 = itemView?.findViewById<ImageView>(R.id.p_stack_img4)
//        val p5 = itemView?.findViewById<ImageView>(R.id.p_stack_img5)
//
//
//        fun bind(itemProject : ProjectData? , context: Context){
//            val urlString = itemProject?.ptitile.toString()
//            if(!urlString.isEmpty()){
//                //ptitle?.setImageResource(R.mipmap.ic_launcher) //photo->내title : 기본 이미지 설정
//
//            }else{
//                ptitle?.visibility = View.GONE
//            }
//            ptitle?.text = itemProject?.ptitile
//            pdday?.text = itemProject?.pdday
//            pnum?.append("x${itemProject?.pnum}")
//            ppart?.text = itemProject?.ppart
//            //plangs?. = itemProject?.imagelist //이미지 url 하나씩
//
//        }
//
//    }
//
//
//    }
//    /*
//    override fun onCreateViewHoler(parent: ViewGroup, viewType:Int ): ViewHolder{
//        return RecyclerAdapter.ViewHolder(LayoutInflater.from(context)
//            .inflate(R.layout.projectlist_item,parent,false))
//
//    }
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.bind(resultList[position],context)
//    }
//    override fun getItemCount(): Int {
//        return resultList.count()
//    }
//
//
//    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!){
//        val ptitle = itemView?.findViewById<TextView>(R.id.ptitle)
//        val pdday = itemView?.findViewById<TextView>(R.id.pdday)
//        val pnum = itemView?.findViewById<TextView>(R.id.pNum)
//        val ppart = itemView?.findViewById<TextView>(R.id.ppartlist)
//
//        val plangs = itemView?.findViewById<LinearLayout>(R.id.imagelist)
//        val p1 = itemView?.findViewById<ImageView>(R.id.p_stack_img1)
//        val p2 = itemView?.findViewById<ImageView>(R.id.p_stack_img2)
//        val p3 = itemView?.findViewById<ImageView>(R.id.p_stack_img3)
//        val p4 = itemView?.findViewById<ImageView>(R.id.p_stack_img4)
//        val p5 = itemView?.findViewById<ImageView>(R.id.p_stack_img5)
//
//
//        fun bind(itemProject : ProjectData? , context: Context){
//            val urlString = itemProject?.ptitile.toString()
//            if(!urlString.isEmpty()){
//                //ptitle?.setImageResource(R.mipmap.ic_launcher) //photo->내title : 기본 이미지 설정
//
//            }else{
//                ptitle?.visibility = View.GONE
//            }
//            ptitle?.text = itemProject?.ptitile
//            pdday?.text = itemProject?.pdday
//            pnum?.append("x${itemProject?.pnum}")
//            ppart?.text = itemProject?.ppart
//            //plangs?. = itemProject?.imagelist //이미지 url 하나씩
//
//        }
//
//    }
//    */
//
