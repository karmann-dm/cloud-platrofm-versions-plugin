package com.karmanno.plugins.utils;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.gradle.api.Project;

import java.io.File;
import java.io.IOException;

public class GitUtils {

    public static Git gitRepo(Project project) {
        try {
            File gitDir = getRootGitDir(project.getProjectDir());
            return Git.wrap(new FileRepository(gitDir));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static File getRootGitDir(File currentRoot) {
        File gitDir = scanForRootGitDir(currentRoot);
        if (!gitDir.exists()) {
            throw new IllegalArgumentException("Cannot find '.git' directory");
        }
        return gitDir;
    }

    private static File scanForRootGitDir(File currentRoot) {
        File gitDir = new File(currentRoot, ".git");
        if (gitDir.exists()) {
            return gitDir;
        }
        if (currentRoot.getParentFile() == null) {
            return gitDir;
        }
        return scanForRootGitDir(currentRoot.getParentFile());
    }
}
