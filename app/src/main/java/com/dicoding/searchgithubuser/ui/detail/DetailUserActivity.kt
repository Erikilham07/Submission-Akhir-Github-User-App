package com.dicoding.searchgithubuser.ui.detail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.dicoding.searchgithubuser.R
import com.dicoding.searchgithubuser.data.remote.Result
import com.dicoding.searchgithubuser.databinding.ActivityDetailUserBinding
import com.google.android.material.tabs.TabLayoutMediator

class DetailUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailUserBinding
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.title = resources.getString(R.string.app_name2)

        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        val detailViewModel: DetailViewModel by viewModels{
            factory
        }

        username = intent.getStringExtra(EXTRA_USER) as String

        detailViewModel.getUser(username)
        val sectionsPagerAdapter = SectionPagerAdapter(this)
        sectionsPagerAdapter.username = username

        binding.viewPager.adapter = sectionsPagerAdapter
        supportActionBar?.elevation = 0f

        detailViewModel.getUser(username).observe(this) { result ->
            if (result != null){
                when(result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val user = result.data
                        binding.apply {
                            txName.text = user.name
                            txUsername.text = user.username
                            txCompany.text = user.company
                            txLocation.text = user.location
                            txRepository.text = resources.getString(R.string.Repository, user.repository)
                            if (user.isFavourite) {
                                tvFavourite.setImageDrawable(ContextCompat.getDrawable(tvFavourite.context, R.drawable.ic_blue_favorite))
                            } else {
                                tvFavourite.setImageDrawable(ContextCompat.getDrawable(tvFavourite.context, R.drawable.ic_blue_favorite_border))
                            }
                            tvFavourite.setOnClickListener {
                                if (user.isFavourite){
                                    detailViewModel.deleteFavourite(user)
                                } else {
                                    detailViewModel.saveFavourite(user)
                                }
                            }
                        }
                        Glide.with(this)
                            .load(user.avatar)
                            .circleCrop()
                            .into(binding.imgAvatar)
                        val countFollow = arrayOf(user.followers, user.following)
                        TabLayoutMediator(binding.tab, binding.viewPager) { tab, position ->
                            tab.text = resources.getString(TAB_TITLES[position], countFollow[position])
                        }.attach()
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            this,
                            "Terjadi kesalahan" + result.error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        detailViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        detailViewModel.toastText.observe(this) {
            it.getContentIfNotHandled()?.let { toastText ->
                Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show()
            }
        }
    } }

        override fun onCreateOptionsMenu(menu: Menu): Boolean {
            val inflater = menuInflater
            inflater.inflate(R.menu.detail_menu, menu)
            return true
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.share -> {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.share, username))
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)
                    true
                }
                else -> true
            }
        }


    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val EXTRA_USER = "extra_user"
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2
        )
    }
}