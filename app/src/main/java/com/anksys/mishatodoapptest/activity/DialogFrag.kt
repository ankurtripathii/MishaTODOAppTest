package com.anksys.mishatodoapptest.activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import com.anksys.mishatodoapptest.R
import com.bumptech.glide.Glide
class DialogFragment : DialogFragment() {
    lateinit var tvtitle:TextView
    lateinit var tvdesc:TextView
    lateinit var tvttime:TextView
    lateinit var imgUri:String
    lateinit var img:ImageView
    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.dialog_fragment, container, false)
        tvtitle = view.findViewById(R.id.dialogtitle)
        tvdesc = view.findViewById(R.id.dialogdesc)
        tvttime = view.findViewById(R.id.dialogtimestamp)
        img = view.findViewById(R.id.image)
        arguments?.let {
            tvtitle.text = it.getString("title").toString()
            tvdesc.text = it.getString("title").toString()
            tvttime.text = it.getString("time").toString()
            imgUri = it.getString("imgUri").toString()
            Glide.with(requireContext())
                .load(imgUri)
                .into(img)
        }
        return view
    }
}