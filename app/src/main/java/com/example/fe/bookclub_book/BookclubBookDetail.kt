package com.example.fe.bookclub_book

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.fe.BookDetail.BookDetailActivity
import com.example.fe.JohnRetrofitClient
import com.example.fe.bookclub_book.adapter.BookclubBookDetailMemberRVAdapter
import com.example.fe.bookclub_book.server.BookClubDetailResponse
import com.example.fe.bookclub_book.server.BookClubRetrofitInterface
import com.example.fe.databinding.ActivityBookclubBookDetailBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookclubBookDetail: AppCompatActivity() {

    private lateinit var binding: ActivityBookclubBookDetailBinding
    private lateinit var bookclubDetailMemberRVAdapter: BookclubBookDetailMemberRVAdapter
    private var itemLink: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBookclubBookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            finish()
        }

        // Intent로부터 bookClubId를 가져옵니다.
        val bookClubId = intent.getIntExtra("bookClubId", -1)
        if (bookClubId != -1) {
            fetchBookClubDetail(bookClubId)
        } else {
            finish()
        }

        binding.certifyBtn.setOnClickListener {
            val intent = Intent(this, BookClubCertification::class.java)
            intent.putExtra("bookClubId", bookClubId) // bookClubId를 넘겨줌
            intent.putExtra("BOOK_ID", bookClubId)
            startActivity(intent)
        }

        val bookId = intent.getIntExtra("BOOK_ID",-1)

        binding.detailBookOverNextBtn.setOnClickListener {
            val intent = Intent(this, BookDetailActivity::class.java)
            intent.putExtra("BOOK_ID", bookId.toLong()) // bookId를 넘겨줌
            startActivity(intent)
        }

        binding.bookTitleTv.apply {
            isSelected = true
        }

        // "자세히 보기" 텍스트뷰 클릭 리스너 설정
        binding.detailMoreInfoTv.setOnClickListener {
            itemLink?.let { link ->
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                startActivity(browserIntent)
            }
        }

        initBookclubDetailMemberRecyclerview()
    }

    // 북클럽 상세 화면 조회 함수
    private fun fetchBookClubDetail(bookClubId: Int) {
        val api = JohnRetrofitClient.getClient(this).create(BookClubRetrofitInterface::class.java)
        api.getBookClubDetail(bookClubId).enqueue(object : Callback<BookClubDetailResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<BookClubDetailResponse>, response: Response<BookClubDetailResponse>) {
                if (response.isSuccessful) {
                    val bookclubDetail = response.body()
                    bookclubDetail?.let {
                        binding.bookTitleTv.text = it.result.bookInfo.title
                        binding.bookAuthorTv.text = it.result.bookInfo.author
                        binding.publisherTv.text = it.result.bookInfo.publisher
                        binding.myCompletionProgressBar.progress = it.result.myCompletionRate
                        binding.myCompletionProgressTv.text = "${it.result.myCompletionRate}%"
                        binding.elapseWeekTv.text = "${it.result.elapsedWeeks}주차 추천 완독률"
                        binding.recommendCompletionProgressBar.progress = it.result.recommendedCompletionRate
                        binding.recommendCompletionProgressTv.text = "${it.result.recommendedCompletionRate}%"

                        Glide.with(this@BookclubBookDetail)
                            .load(it.result.bookInfo.cover)
                            .into(binding.bookIv)

                        Glide.with(this@BookclubBookDetail)
                            .load(it.result.bookInfo.cover)
                            .into(binding.bookBgIv)

                        bookclubDetailMemberRVAdapter.setMembers(it.result.members)

//                        // 인증 버튼의 visibility 설정
//                        if (it.result.elapsedWeeks == 4) {
//                            binding.certifyBtn.visibility = View.VISIBLE
//                        } else {
//                            binding.certifyBtn.visibility = View.INVISIBLE
//                        }

                        itemLink = it.result.bookInfo.itemLink

                    }
                } else {
                    // 오류 처리
                    println("Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<BookClubDetailResponse>, t: Throwable) {
                println("Network Error: ${t.message}")
            }
        })
    }

    // 북클럽 상세 화면 멤버 조회 함수
    private fun initBookclubDetailMemberRecyclerview() {
        binding.bookclubDetailMemberRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        bookclubDetailMemberRVAdapter = BookclubBookDetailMemberRVAdapter()
        binding.bookclubDetailMemberRv.adapter = bookclubDetailMemberRVAdapter
    }

}