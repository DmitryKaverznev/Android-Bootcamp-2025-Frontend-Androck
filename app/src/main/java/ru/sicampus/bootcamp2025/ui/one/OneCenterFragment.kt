package ru.sicampus.bootcamp2025.ui.one

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import ru.sicampus.bootcamp2025.R
import ru.sicampus.bootcamp2025.data.one.OneCenterImpl
import ru.sicampus.bootcamp2025.data.one.OneNetworkDataSource
import ru.sicampus.bootcamp2025.data.save.AuthStorageDataSource
import ru.sicampus.bootcamp2025.databinding.FragmentOneCenterBinding
import ru.sicampus.bootcamp2025.domain.one.GetUsersUseCase
import ru.sicampus.bootcamp2025.domain.one.OneCenter
import ru.sicampus.bootcamp2025.domain.one.RegisterUserUseCase

class OneCenterFragment : Fragment(R.layout.fragment_one_center) {

    private var _binding: FragmentOneCenterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OneCenterViewModel by viewModels {
        val center = parseArguments()
        val authStorage = AuthStorageDataSource()
        val networkDataSource = OneNetworkDataSource(authStorage)
        val repository = OneCenterImpl(networkDataSource, center.name)

        OneCenterViewModel.factory(
            oneCenter = center,
            getUsersUseCase = GetUsersUseCase(repository),
            registerUserUseCase = RegisterUserUseCase(repository)
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOneCenterBinding.bind(view)

        with(binding) {
            register.setOnClickListener { viewModel.registerUser() }
            setupRecyclerView()
            observeViewModel()
            viewModel.loadUsers()
        }
    }

    private fun parseArguments(): OneCenter {
        return requireArguments().let {
            OneCenter(
                name = it.getString(ARG_NAME) ?: "",
                description = it.getString(ARG_DESCRIPTION) ?: "",
                coordinateX = it.getDouble(ARG_LATITUDE).toString(),
                coordinateY = it.getDouble(ARG_LONGITUDE).toString()
            )
        }
    }

    private fun setupRecyclerView() {
        binding.usersRecyclerView.apply {
            adapter = UserAdapter(emptyList()) { viewModel.registerUser() }
            layoutManager = LinearLayoutManager(requireContext())
        }

        val arg = parseArguments();

        binding.title.text = parseArguments().name
        binding.description.text = arg.description
        binding.coordinates.text = getString(
            R.string.coordinates_format,
            arg.coordinateX.toDouble(),
            arg.coordinateY.toDouble(),
        )
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is OneCenterViewModel.State.Loading -> showLoading()
                        is OneCenterViewModel.State.Loaded -> showData(state)
                        is OneCenterViewModel.State.Error -> showError(state.message)
                    }
                }
            }
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.usersRecyclerView.visibility = View.GONE
    }

    private fun showData(state: OneCenterViewModel.State.Loaded) {
        binding.progressBar.visibility = View.GONE
        binding.usersRecyclerView.visibility = View.VISIBLE
        (binding.usersRecyclerView.adapter as UserAdapter).submitList(state.volunteers)
    }

    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_NAME = "name"
        private const val ARG_DESCRIPTION = "description"
        private const val ARG_LATITUDE = "latitude"
        private const val ARG_LONGITUDE = "longitude"

        fun newInstance(center: OneCenter) = OneCenterFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_NAME, center.name)
                putString(ARG_DESCRIPTION, center.description)
                putDouble(ARG_LATITUDE, center.coordinateX.toDouble())
                putDouble(ARG_LONGITUDE, center.coordinateY.toDouble())
            }
        }
    }
}