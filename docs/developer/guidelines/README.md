# Guidelines

## General

* Work on the project repositories following the [forking workflow](https://www.atlassian.com/git/tutorials/comparing-workflows/forking-workflow)
* Follow the [Kitodo coding guidelines](https://www.kitodo.org/fileadmin/groups/kitodo/Dokumente/Kitodo-EntwicklerLeitfaden_2017-06.pdf)
* Features to be developed should be announced as an issue before the corresponding pull request is opened
* Notes on fork branches:
  * each branch should be self-contained and include only the changes that are necessary
  * working on a feature can be done privately in personal forks or, by several people, in a fork of a GitHub organization

## Commits

* in English, following e.g. the recommendations in [How to Write a Git Commit Message](http://chris.beams.io/posts/git-commit/)
* a commit should only contain the changes that are described in the commit message
* prefer many small commits with few changes each over few large, extensive commits

## Pull requests

* ideally, the changes should be reviewed by another person than the author on GitHub
* at the time of merging, the pull request must integrate cleanly; conflicts have to be resolved by the author

## Branches

The description below refers to the [Kitodo.Production](https://github.com/kitodo/kitodo-production) project (see [supported versions](https://github.com/kitodo/kitodo-production/blob/main/SUPPORTED_VERSIONS.md) for which releases are maintained).

* **branch `main`**: the active development branch. All new features and changes for the current version (4.x) are developed here
* **maintenance branches** (for example `3.9.x`): older releases that are still being maintained with security fixes only. Changes for these releases are backported from `main` or developed directly in the maintenance branch

All other historical branches (for example `2.x`) are no longer maintained and are kept for reference only.
