package com.example.fe.scrap

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import android.view.ViewGroup.LayoutParams
import com.example.fe.R
import com.example.fe.databinding.FragmentNewScrapDialogBinding
import com.example.fe.databinding.FragmentScrapCustomToastBinding

class NewScrapDialogFragment(
    private val onScrapCreated: () -> Unit // 북마크 상태 변경 콜백 추가
) : DialogFragment() {

    private lateinit var binding: FragmentNewScrapDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewScrapDialogBinding.inflate(inflater, container, false)
        initListeners()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            LayoutParams.WRAP_CONTENT, // 다이얼로그의 가로 크기
            LayoutParams.WRAP_CONTENT // 다이얼로그의 세로 크기
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 배경 투명 처리
    }

    private fun initListeners() {
        // "완료" 버튼 클릭 시
        binding.newScrapCompeleteBtn.setOnClickListener {
            val scrapName = binding.scrapNameEt.text.toString().trim()

            if (scrapName.isNotEmpty()) {
                showCustomToast(scrapName) // 커스텀 토스트 실행
                onScrapCreated() // 북마크 상태 변경 콜백 호출
                dismiss() // 다이얼로그 닫기
            } else {
                Toast.makeText(requireContext(), "스크랩명을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        // "취소" 버튼 클릭 시
        binding.newScrapCancleBtn.setOnClickListener {
            dismiss() // 다이얼로그 닫기
        }
    }

    private fun showCustomToast(scrapName: String) {
        val inflater = LayoutInflater.from(requireContext())
        val toastBinding = FragmentScrapCustomToastBinding.inflate(inflater)

        toastBinding.scrapItemIv.setImageResource(R.drawable.img_scrap_user_add) // 아이콘 설정
        toastBinding.scrapItemNameTv.text = "${scrapName}에 저장됨"

        // 토스트 객체 생성
        val toast = Toast(requireContext()).apply {
            duration = Toast.LENGTH_SHORT
            setView(toastBinding.root)
            setGravity(android.view.Gravity.TOP, 0, 200) // 위치 조정: 상단에 표시
        }

        toast.show()
    }
}
