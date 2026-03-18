package com.fitnessultra.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.fitnessultra.R
import com.fitnessultra.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var runAdapter: RunAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        runAdapter = RunAdapter(
            onItemClick = { run ->
                val bundle = Bundle().apply { putLong("runId", run.id) }
                findNavController().navigate(R.id.action_historyFragment_to_chartsFragment, bundle)
            },
            onReplayClick = { run ->
                val bundle = Bundle().apply { putLong("runId", run.id) }
                findNavController().navigate(R.id.action_historyFragment_to_replayFragment, bundle)
            }
        )

        binding.rvRuns.apply {
            adapter = runAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        val swipeToDelete = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val run = runAdapter.currentList[viewHolder.adapterPosition]
                viewModel.deleteRun(run)
                Snackbar.make(requireView(), "Run deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo") { viewModel.restoreRun(run) }
                    .show()
            }
        }
        ItemTouchHelper(swipeToDelete).attachToRecyclerView(binding.rvRuns)

        viewModel.runs.observe(viewLifecycleOwner) { runs ->
            runAdapter.submitList(runs)
            binding.tvEmpty.visibility = if (runs.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
