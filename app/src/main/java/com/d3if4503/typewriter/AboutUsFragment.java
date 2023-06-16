package com.d3if4503.typewriter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.aexpress.databinding.FragmentAboutUsBinding;
import com.squareup.picasso.Picasso;

public class AboutUsFragment extends Fragment {

    private FragmentAboutUsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = FragmentAboutUsBinding.inflate(getLayoutInflater());

        binding.viewOnMapButton.setOnClickListener(this::openMap);

        // Mengganti spasi pada alamat perusahaan dengan karakter '+' agar sesuai dengan format URL
        String companyAddress = "Jl. Contoh No. 123, Kota Anda, Negara Anda";
        String encodedAddress = companyAddress.replace(" ", "+");

        // Membentuk URL untuk Static Maps API
        String staticMapsUrl = "https://maps.googleapis.com/maps/api/staticmap?center=" + encodedAddress + "&zoom=15&size=640x320&key=YOUR_API_KEY";

        // Menggunakan Library Picasso untuk memuat gambar dari URL ke ImageView
        Picasso.get().load(staticMapsUrl).into(binding.companyMapImage);

        return binding.getRoot();
    }

    public void openMap(View view) {
        String mapsUrl = "https://www.google.com/maps/place/Nugraha+Offset/@-6.9901599,107.6268107,17.14z/data=!4m10!1m2!2m1!1sNugraha+Offset!3m6!1s0x2e68e99f4fbc4447:0x2b99487b94c2a617!8m2!3d-6.9901599!4d107.6268107!15sCg5OdWdyYWhhIE9mZnNldJIBCnByaW50X3Nob3DgAQA!16s%2Fg%2F11b6g7k03n?entry=ttu";
        Uri mapsUri = Uri.parse(mapsUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, mapsUri);

        // Pengecekan apakah ada aplikasi yang dapat menangani intent ini
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // Jika tidak ada aplikasi yang dapat menangani intent, buka URL langsung di browser
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapsUrl));
            startActivity(browserIntent);
        }
    }


//    public void openMap(View view) {
//        Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/place/Nugraha+Offset/@-6.9901599,107.6268107,17.14z/data=!4m10!1m2!2m1!1sNugraha+Offset!3m6!1s0x2e68e99f4fbc4447:0x2b99487b94c2a617!8m2!3d-6.9901599!4d107.6268107!15sCg5OdWdyYWhhIE9mZnNldJIBCnByaW50X3Nob3DgAQA!16s%2Fg%2F11b6g7k03n?entry=ttu");
//        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//        mapIntent.setPackage("com.google.android.apps.maps");
//
//        // Pengecekan apakah ada aplikasi yang dapat menangani intent ini
//        if (mapIntent.resolveActivity(requireContext().getPackageManager()) != null) {
//            startActivity(mapIntent);
//        } else {
//            // Jika tidak ada aplikasi yang dapat menangani intent, tampilkan pesan kesalahan
//            Toast.makeText(getActivity(), "Aplikasi Google Maps tidak ditemukan", Toast.LENGTH_SHORT).show();
//        }
//    }


//    public void openMap(View view) {
//        Uri gmmIntentUri = Uri.parse("geo:-6.9901599,107.6268107?q=Nugraha+Offset");
//        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//        mapIntent.setPackage("com.google.android.apps.maps");
//
//        // Pengecekan apakah ada aplikasi yang dapat menangani intent ini
//        if (mapIntent.resolveActivity(requireContext().getPackageManager()) != null) {
//            startActivity(mapIntent);
//        } else {
//            // Jika tidak ada aplikasi yang dapat menangani intent, tampilkan pesan kesalahan
//            Toast.makeText(requireContext(), "Aplikasi Google Maps tidak ditemukan", Toast.LENGTH_SHORT).show();
//        }
//    }

//    public void openMap(View view) {
//        Uri gmmIntentUri = Uri.parse("https://goo.gl/maps/R3rRceKqes5UwLWr5");
//        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//        mapIntent.setPackage("com.google.android.apps.maps");
//
//        // Pengecekan apakah ada aplikasi yang dapat menangani intent ini
//        if (mapIntent.resolveActivity(getContext().getPackageManager()) != null) {
//            startActivity(mapIntent);
//        } else {
//            // Jika tidak ada aplikasi yang dapat menangani intent, tampilkan pesan kesalahan
//            Toast.makeText(getActivity(), "Aplikasi Google Maps tidak ditemukan", Toast.LENGTH_SHORT).show();
//        }
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
