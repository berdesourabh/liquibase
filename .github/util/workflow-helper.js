// -----------------------------------------------------------------
// THIS FILE SHOULD BE MANUALLY COPY/PASTED ACROSS ALL THE USED REPOSITORIES ON CHANGES SO THEY STAY IN SYNC
// -----------------------------------------------------------------

module.exports = ({github, context}) => {

    return {
        getCurrentBranch: function() {
            return context.payload.pull_request.head.ref;
        },

        findMatchingBranch: async function (owner, repo, branchesToCheck) {
            if (!branchesToCheck) {
                branchesToCheck = [context.payload.pull_request.head.ref, context.payload.pull_request.base.ref, "main", "master"]
            }

            for (let branchName of branchesToCheck) {
                if (!branchName) {
                    console.log("Skipping empty branch name: " + branchName);
                    continue
                }

                branchName = branchName
                    .replace("refs/heads/", "")
                    .replace("refs/heads/tags", "");
                try {
                    let branch = await github.repos.getBranch({
                        "owner": owner,
                        "repo": repo,
                        "branch": branchName,
                    });

                    let returnData = {
                        "name": branchName,
                        "sha": branch.data.commit.sha,
                    };

                    //add PR info
                    let pulls = await github.pulls.list({
                        "owner": owner,
                        "repo": repo,
                        "head": `${owner}:${branchName}`,
                        "per_page": 1,
                        "page": 1,
                    });

                    if (pulls.data.length === 0) {
                        console.log(`No pull request for branch ${branchName}`);
                    } else {
                        console.log(`Found PR for branch ${branchName}`);

                        returnData.pullRequestId = pulls.data[0].number;
                        returnData.pullRequestState = pulls.data[0].state;
                    }

                    //add build info
                    let runs = await github.actions.listWorkflowRuns({
                        "owner": owner,
                        "repo": repo,
                        "workflow_id": "build.yml",
                        "branch": branchName,
                        "per_page": 1,
                        "page": 1,
                    });

                    if (runs.data.workflow_runs.length === 0) {
                        console.log(`No build for branch ${branchName}`);
                    } else {
                        console.log(`Found build for branch ${branchName}`);

                        let run = runs.data.workflow_runs[0];

                        returnData.runNumber = run.run_number;
                        returnData.runStatus = run.status;
                        returnData.runConclusion = run.conclusion;
                        returnData.runHtmlUrl = run.htmlUrl;
                    }


                    console.log("Matching branch information: ");
                    console.log(returnData);

                    return returnData
                } catch (error) {
                    if (error.status === 404) {
                        //try next branch
                        console.log(`No branch ${branchName}`);
                    } else {
                        throw (`Checking branch ${branchName} returned ${error.status}`);
                    }
                }
            }
        }
    }
}
