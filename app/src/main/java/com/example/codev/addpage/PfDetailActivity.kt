package com.example.codev.addpage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.codev.AndroidKeyStoreUtil
import com.example.codev.databinding.ActivityPfDetailBinding

class PfDetailActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityPfDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityPfDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        AndroidKeyStoreUtil.init(this)

    }
}