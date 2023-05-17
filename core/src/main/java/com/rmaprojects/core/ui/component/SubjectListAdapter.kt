package com.rmaprojects.core.ui.component

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.rmaprojects.core.data.source.remote.response.grades.GradeViewEntity
import com.rmaprojects.core.databinding.ItemGradingSubjectBinding
import com.rmaprojects.core.domain.model.GradeInput

class SubjectListAdapter<T>(
    private val subjectList: List<T>,
    private val onGradeChanged: ((subjectId: Int, grade: String) -> Unit)?
): RecyclerView.Adapter<SubjectListAdapter.SubjectListViewHolder<T>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectListViewHolder<T> {
        val binding = ItemGradingSubjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubjectListViewHolder(binding)
    }

    override fun getItemCount(): Int = subjectList.size

    override fun onBindViewHolder(holder: SubjectListViewHolder<T>, position: Int) {
        val subjectItem = subjectList[position]
        holder.bind(subjectItem, onGradeChanged)
    }

    class SubjectListViewHolder<T>(
        private val binding: ItemGradingSubjectBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(subjectItem: T, onGradeChanged: ((subjectId: Int, grade: String) -> Unit)?) {
            if (subjectItem is GradeInput) {
                binding.txtSubjectName.text = subjectItem.subjectName
                binding.inputGrade.editText?.doOnTextChanged { text, _, _, _ ->
                    onGradeChanged?.invoke(
                        subjectItem.subjectId,
                        text.toString()
                    )
                }
                return
            }
            if (subjectItem is GradeViewEntity) {
                binding.txtSubjectName.text = subjectItem.nameSubject
                binding.inputGrade.isVisible = false
                binding.txtGrade.isVisible = true
                return
            }
        }
    }
}