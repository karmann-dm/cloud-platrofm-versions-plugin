package com.karmanno.plugins.tasks;

import com.karmanno.plugins.utils.GitUtils;
import com.karmanno.plugins.domain.VersionInfo;
import com.karmanno.plugins.services.VersionsService;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.impldep.org.eclipse.jgit.api.Git;
import org.gradle.internal.impldep.org.eclipse.jgit.lib.Constants;
import org.gradle.internal.impldep.org.eclipse.jgit.lib.ObjectId;
import org.gradle.internal.impldep.org.eclipse.jgit.lib.Ref;
import org.gradle.internal.impldep.org.eclipse.jgit.revwalk.RevCommit;

import java.util.List;

public class PrintVersionTask extends DefaultTask {
    @TaskAction
    public void doTask() {
        Project project = getProject();
        Git git = GitUtils.gitRepo(project);

        try {
            List<Ref> tagList = git.tagList().call();
            Ref latestTag = tagList.get(tagList.size() - 1);
            VersionInfo latestVersionInfo = VersionInfo.fromTagString(latestTag.getName());

            ObjectId latestTagId = latestTag.getObjectId();
            ObjectId headId = git.getRepository().resolve(Constants.HEAD);
            Iterable<RevCommit> commitsBetweenHeadAndLatestTag = git.log().addRange(latestTagId, headId).call();

            new VersionsService()
                    .calculateNewVersions(latestVersionInfo, commitsBetweenHeadAndLatestTag)
                    .printVersion();
        } catch (Exception e) {
            new VersionInfo()
                    .printVersion();
        }
    }
}