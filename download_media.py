import os
import urllib.request

videos = [
    ("subah_ka_manglacharan.mp4", "https://www.w3schools.com/html/mov_bbb.mp4"),
    ("subah_ka_nitya_niyam.mp4", "https://www.w3schools.com/html/movie.mp4"),
    ("asur_nikandan_rameni.mp4", "https://www.w3schools.com/html/mov_bbb.mp4"),
    ("sandhya_aarti.mp4", "https://www.w3schools.com/html/movie.mp4"),
    ("annadev_ki_aarti.mp4", "https://www.w3schools.com/html/mov_bbb.mp4"),
    ("raksha_mantra.mp4", "https://www.w3schools.com/html/movie.mp4"),
    ("tajpur_delhi_satsang_2000.mp4", "https://www.w3schools.com/html/mov_bbb.mp4")
]

os.makedirs("app/src/main/assets", exist_ok=True)

# Remove old .mp3 files if they exist to keep the assets clean
for fname in os.listdir("app/src/main/assets"):
    if fname.endswith(".mp3"):
        try:
            os.remove(f"app/src/main/assets/{fname}")
            print(f"Removed old asset: {fname}")
        except Exception as re:
            pass

for fname, url in videos:
    target_path = f"app/src/main/assets/{fname}"
    print(f"Downloading {url} to {fname}...")
    try:
        req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)'})
        with urllib.request.urlopen(req, timeout=30) as response, open(target_path, 'wb') as out_file:
            out_file.write(response.read())
        print(f"Successfully downloaded {fname}!")
    except Exception as e:
        print(f"Failed to download {fname}: {e}")
        # Create a simple valid fallback to prevent crash
        with open(target_path, 'wb') as empty_file:
            empty_file.write(b'\x00' * 1024)
        print(f"Created a simple local placeholder file for {fname}!")


