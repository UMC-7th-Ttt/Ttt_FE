package com.example.fe.mypage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.fe.JohnRetrofitClient
import com.example.fe.databinding.FragmentMypageBinding
import com.example.fe.mypage.adapter.MyPageVPAdapter
import com.example.fe.mypage.server.MyPageRetrofitInterface
import com.example.fe.mypage.server.UserResponse
import com.example.fe.search.SearchMainActivity
import com.example.fe.setting.Setting
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageFragment : Fragment() {

    lateinit var binding: FragmentMypageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageBinding.inflate(inflater, container, false)

        fetchUser()

        val tabLayout: TabLayout = binding.mypageContentTl
        val viewPager: ViewPager2 = binding.mypageContentVp

        // Set up the ViewPager adapter
        val adapter = MyPageVPAdapter(this)
        viewPager.adapter = adapter

        // Link TabLayout and ViewPager
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "서평"
                1 -> "스크랩"
                else -> null
            }
        }.attach()

        // 설정 아이콘 클릭 리스너 설정
        binding.mypageSettingIv.setOnClickListener {
            context?.let { ctx ->
                val intent = Intent(ctx, Setting::class.java)
                startActivity(intent)
            }
        }

        // 검색 아이콘 클릭 리스너 설정
        binding.mypageSearchIv.setOnClickListener {
            val intent = Intent(context, SearchMainActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    private fun fetchUser() {
        val api = JohnRetrofitClient.getClient(requireContext()).create(MyPageRetrofitInterface::class.java)
        api.getUser().enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val userResponse = response.body()
                    userResponse?.let {
                        binding.mypageNicknameTv.text = it.result.nickname

                        Glide.with(this@MyPageFragment)
                            .load(it.result.profileUrl)
                            .into(binding.mypageCharacterIv)
                    }
                } else {
                    Log.e("MyPageFragment", "Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.e("MyPageFragment", "Network Error: ${t.message}")
            }
        })
    }
}
