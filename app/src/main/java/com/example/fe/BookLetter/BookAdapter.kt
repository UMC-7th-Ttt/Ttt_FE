package com.example.fe.BookLetter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.fe.BookDetail.BookDetailActivity
import com.example.fe.BookLetter.BookDetail
import com.example.fe.databinding.ItemLetterBinding

class BookAdapter(private val bookList: List<BookDetail>) :
    RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    class BookViewHolder(val binding: ItemLetterBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemLetterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = bookList[position]
        with(holder.binding) {


            numbering.text = (position + 1).toString() // ✅ 넘버링 적용 (1부터 시작)


            bookTitleTv.text = book.title // 책 제목
            bookAuthorTv.text = book.author // 작가 이름
            publisherTv.text = book.publisher // 출판사 이름
            bookExcerpt.text = book.description // 구절
            category.text = book.categoryName //
//            btnCategory.text = book.categoryName // 카테고리
//            btnAuthor.text = book.author // 버튼에 들어가는 작가 연결
//            btnPage.text = "${book.itemPage}쪽"
//
//            btnEbook.text = "E북 등록" // 버튼 텍스트 설정
//
//            // ✅ 값이 없는 경우 버튼을 숨김 (GONE 처리)
//            btnAuthor.visibility = if (book.author.isNullOrBlank()) View.GONE else View.VISIBLE
//            btnPage.visibility = if (book.itemPage == 0) View.GONE else View.VISIBLE
//            btnCategory.visibility = if (book.categoryName.isNullOrBlank()) View.GONE else View.VISIBLE
//            btnEbook.visibility = if (!book.hasEbook) View.GONE else View.VISIBLE

            // ✅ Glide로 책 표지 로드 (책 표지와 배경을 동일하게 적용)
            Glide.with(root.context)
                .load(book.cover)
                .into(bookIv) // 책 표지 적용

            // ✅ 배경 이미지를 bookIv와 동일하게 설정 + 확대 + 어둡게
            Glide.with(root.context)
                .load(book.cover)
                .transform(com.bumptech.glide.load.resource.bitmap.CenterCrop()) // 확대 적용
                .into(bookBgIv)

            // ✅ 배경을 어둡게 만들기 (반투명 View 추가)
            bookBgIv.alpha = 0.5f // 50% 투명도 적용

            //알라딘 이동
            detailButton.setOnClickListener {
                val context = root.context
                val aladinUrl = "https://www.aladin.co.kr/search/wsearchresult.aspx?SearchTarget=All&SearchWord=${Uri.encode(book.title)}"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(aladinUrl))
                context.startActivity(intent)
            }

            // 클릭하면 상세 페이지 이동
            bookInfoNextBtn.setOnClickListener {
                val context = root.context
                val intent = Intent(context, BookDetailActivity::class.java)
                intent.putExtra("BOOK_ID", book.bookId) // 책 ID 전달

                intent.putExtra("ITEM_NUMBER", position + 1) // ✅ 넘버링 데이터 전달
                context.startActivity(intent)
            }
        }


    }


    override fun getItemCount(): Int = bookList.size
}
