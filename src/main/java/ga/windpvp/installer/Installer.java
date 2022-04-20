package ga.windpvp.installer;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.kohsuke.github.GHAsset;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

public class Installer {

	private GitHub github;
	private GHRepository repository;

	private final String repositoryString = "Wind-Development/WindSpigot";

	public static void main(String[] args) {
		try {
			new Installer().update();
		} catch (Exception e) {
			System.out.println("An error occured whilst updating WindSpigot!");
			e.printStackTrace();
		}
	}

	// Quick logging
	private void log(String msg) {
		System.out.println(msg);
	}

	// Updates WindSpigot
	private void update() throws Exception {
		// Connect to github anonymously, we are only downloading a file
		github = GitHub.connectAnonymously();
		// Set the download repository
		repository = github.getRepository(repositoryString);
		// Get the releases
		List<GHRelease> releases = repository.listReleases().toList();
		// Get the latest release
		GHRelease latestRelease = repository.getLatestRelease();
		// Ensure not a prerelease
		boolean isPre = latestRelease.isPrerelease();

		// Get the assets list
		List<GHAsset> assetList = latestRelease.listAssets().toList();

		// We want to download the latest stable release, not the latest prerelease
		if (isPre) {
			int latestStableReleaseIndex = releases.size() - 2;
			// size - 1 is the index of the last object, so
			// size - 2 is the index of the 2nd last
			// object

			// Set the release to the latest stable release
			latestRelease = releases.get(latestStableReleaseIndex);
		}

		// The jar asset
		GHAsset jarAsset = null;

		// Find the jar asset
		for (GHAsset asset : assetList) {
			if (asset.getName().contains(".jar")) {
				log("Found artifact " + asset.getName());
				jarAsset = asset;
				break;
			}
		}

		// Get the download url
		String jarUrl = jarAsset.getBrowserDownloadUrl();

		// Log some messages
		log("Downloading WindSpigot " + latestRelease.getTagName() + " from " + jarUrl + "...");
		log("Please wait...");

		InputStream in = new URL(jarUrl).openStream();
		Files.copy(in, Paths.get("WindSpigot.jar"), StandardCopyOption.REPLACE_EXISTING);

		// Log some messages
		log("Download Complete :) This version of WindSpigot now has " + jarAsset.getDownloadCount() + " installs!");
		log("Saved as WindSpigot.jar");
		log("You now have the latest release of WindSpigot installed.");

	}

}
