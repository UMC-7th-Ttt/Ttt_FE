package com.example.fe.bookclub_book

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fe.JohnRetrofitClient
import com.example.fe.R
import com.example.fe.bookclub_book.adapter.CertifyPhotoRVAdapter
import com.example.fe.bookclub_book.server.BookClubCertificationRequest
import com.example.fe.bookclub_book.server.BookClubCertificationResponse
import com.example.fe.bookclub_book.server.BookClubDetailResponse
import com.example.fe.bookclub_book.server.BookClubJoinInfoResponse
import com.example.fe.bookclub_book.server.BookClubRetrofitInterface
import com.example.fe.databinding.ActivityBookclubCertificationBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookClubCertification : AppCompatActivity(), CertifyPhotoRVAdapter.OnPhotoClickListener {
    private lateinit var binding: ActivityBookclubCertificationBinding
    private lateinit var imageAdapter: CertifyPhotoRVAdapter
    private val selectedImages = mutableListOf<Uri>()
    private lateinit var titleEditText: EditText
    private lateinit var reviewEditText: EditText
    private lateinit var pageCountEditText: EditText
    private var bookClubId: Int = -1
    private var bookId: Int = -1
    private var maxPageCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBookclubCertificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            finish()
        }

        // RecyclerView 초기화
        imageAdapter = CertifyPhotoRVAdapter(selectedImages, this)
        binding.photoRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.photoRv.adapter = imageAdapter

        // 이미지 선택 버튼 클릭 리스너
        binding.photoIv1.setOnClickListener {
            // 현재 기기에 설정된 쓰기 권한을 가져오기 위한 변수
            var writePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE )

            // 현재 기기에 설정된 읽기 권한을 가져오기 위한 변수
            var readPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)

            // 읽기 권한과 쓰기 권한에 대해서 설정이 되어있지 않다면
            if (writePermission == PackageManager.PERMISSION_DENIED || readPermission == PackageManager.PERMISSION_DENIED) {
                // 읽기, 쓰기 권한을 요청합니다.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    1
                )
            }

            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, 100)
        }

        titleEditText = findViewById(R.id.title_edit_text)
        reviewEditText = findViewById(R.id.review_edit_text)
        pageCountEditText = findViewById(R.id.page_count_edit_text)

        // EditText 리스트 생성
        val editTexts = listOf(titleEditText, reviewEditText, pageCountEditText)

        fun checkEditTexts() {
            val allFilled = editTexts.all { it.text.isNotEmpty() }
            binding.doneBtn.isEnabled = allFilled
        }

        // EditText에 TextWatcher 추가
        editTexts.forEach { editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    checkEditTexts()
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }

        // EditText 배경 설정 함수
        fun setEditTextBackground(editText: EditText, isFocused: Boolean) {
            if (isFocused) {
                editText.setBackgroundResource(R.drawable.primary_border_radius_8)
            } else {
                editText.setBackgroundResource(R.drawable.edit_text_radius_8)
            }
        }

        // EditText 높이 조절 함수
        fun adjustEditTextHeight(editText: EditText) {
            val lineHeight = editText.lineHeight // 각 줄의 높이
            val lineCount = editText.lineCount // 현재 줄 수
            val padding = editText.paddingTop + editText.paddingBottom // 패딩

            // EditText의 높이를 설정
            editText.layoutParams.height = lineHeight * lineCount + padding
            editText.requestLayout() // 레이아웃 갱신
        }


        titleEditText.setOnClickListener {
            setEditTextBackground(titleEditText, true)
        }

        titleEditText.setOnFocusChangeListener { _, hasFocus ->
            setEditTextBackground(titleEditText, hasFocus)
        }

        titleEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setEditTextBackground(titleEditText, true)
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        // Review EditText 설정
        reviewEditText.setOnClickListener {
            setEditTextBackground(reviewEditText, true)
        }

        reviewEditText.setOnFocusChangeListener { _, hasFocus ->
            setEditTextBackground(reviewEditText, hasFocus)
        }

        reviewEditText.addTextChangedListener(object : TextWatcher {
            var isEditing = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 현재 입력된 텍스트의 길이를 가져옴
                val currentLength = s?.length ?: 0

                // 글자 수 제한
                if (currentLength > 300) {
                    if (!isEditing) { // 편집 중이 아닐 때만 실행
                        isEditing = true // 편집 중으로 설정
                        if (s != null) {
                            reviewEditText.setText(s.subSequence(0, 300))
                        } // 최대 300자까지만 유지
                        reviewEditText.setSelection(300) // 커서 위치 조정
                        isEditing = false // 편집 종료
                        return // 이 시점에서 함수를 종료하여 카운트가 더해지지 않도록 함
                    }
                }

                // Review Count TextView 업데이트
                binding.reviewCountTv.text =  "$currentLength/300"

                // EditText 높이 조절
                adjustEditTextHeight(reviewEditText)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Page Count EditText 설정
        bookId = intent.getIntExtra("BOOK_ID", -1)

        if (bookId != -1) {
            fetchBookClubInfo(bookId)
        } else {
            Toast.makeText(this, "Invalid book ID", Toast.LENGTH_SHORT).show()
        }

        pageCountEditText.setOnClickListener {
            setEditTextBackground(pageCountEditText, true)
        }

        pageCountEditText.setOnFocusChangeListener { _, hasFocus ->
            setEditTextBackground(pageCountEditText, hasFocus)
        }

        pageCountEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setEditTextBackground(pageCountEditText, true)

                // 사용자가 입력할 수 있는 숫자만 허용
                if (!s.isNullOrEmpty() && !s.toString().matches("\\d*".toRegex())) {
                    // 마지막 입력이 숫자가 아닐 경우 입력 내용 지우기
                    pageCountEditText.setText(s.subSequence(0, s.length - 1))
                    pageCountEditText.setSelection(pageCountEditText.text.length)
                    Toast.makeText(this@BookClubCertification, "숫자만 입력할 수 있습니다.", Toast.LENGTH_SHORT).show()
                } else if (!s.isNullOrEmpty()) {
                    // 최대 페이지 수를 넘지 않도록 제한
                    val input = s.toString().toIntOrNull()
                    if (input != null && input > maxPageCount) {
                        pageCountEditText.setText(maxPageCount.toString())
                        pageCountEditText.setSelection(pageCountEditText.text.length)
                        Toast.makeText(this@BookClubCertification, "${maxPageCount}페이지를 넘어갈 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // 인증 버튼 클릭 리스너
        binding.doneBtn.setOnClickListener {
            val title = titleEditText.text.toString()
            val content = reviewEditText.text.toString()
            val currentPage = pageCountEditText.text.toString().toInt()
            val imgUrl = if (selectedImages.isNotEmpty()) selectedImages[0].toString() else ""
            val isSecret = false

            val request = BookClubCertificationRequest(
                title = title,
                content = content,
                currentPage = currentPage,
                imgUrl = imgUrl,
                isSecret = isSecret
            )

            sendCertification(request)
        }

        bookClubId = intent.getIntExtra("bookClubId", -1)
    }

    private fun fetchBookClubInfo(bookId: Int) {
        val api = JohnRetrofitClient.getClient(this).create(BookClubRetrofitInterface::class.java)
        api.getBookClubInfo(bookId).enqueue(object : Callback<BookClubJoinInfoResponse> {
            override fun onResponse(call: Call<BookClubJoinInfoResponse>, response: Response<BookClubJoinInfoResponse>) {
                if (response.isSuccessful) {
                    val bookClubDetailResponse = response.body()
                    bookClubDetailResponse?.let {
                        if (it.isSuccess) {
                            maxPageCount = it.result.bookInfo.itemPage
                            pageCountEditText.hint = "0/${it.result.bookInfo.itemPage}"
                        } else {
                            Toast.makeText(this@BookClubCertification, "Failed to fetch: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this@BookClubCertification, "Failed to fetch: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BookClubJoinInfoResponse>, t: Throwable) {
                Toast.makeText(this@BookClubCertification, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun sendCertification(request: BookClubCertificationRequest) {
        if (bookClubId == -1) {
            return
        }

        val api = JohnRetrofitClient.getClient(this).create(BookClubRetrofitInterface::class.java)
        api.postCertification(bookClubId, request).enqueue(object :
            Callback<BookClubCertificationResponse> {
            override fun onResponse(call: Call<BookClubCertificationResponse>, response: Response<BookClubCertificationResponse>) {
                if (response.isSuccessful) {
                    val certificationResponse = response.body()
                    certificationResponse?.let {
                        if (it.isSuccess) {
                            finish()
                        } else {
                            Toast.makeText(this@BookClubCertification, "Failed to certify: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this@BookClubCertification, "Failed to certify: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BookClubCertificationResponse>, t: Throwable) {
                Toast.makeText(this@BookClubCertification, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                selectedImages.add(uri)
                imageAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onPhotoDeleteClick(position: Int) {
        selectedImages.removeAt(position)
        imageAdapter.notifyItemRemoved(position)
    }
}