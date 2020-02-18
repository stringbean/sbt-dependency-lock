---
name: "Bug fix"
about: For all PRs that fix bugs
title: ''
labels: bug
assignees: ''
---

This PR fixes #.

## Behaviour

### Before

<details>
  <summary>Dependency Check Output</summary>

  ```text
  [output of dependencyLockCheck]
  ```
</details>

### After

<details>
  <summary>Dependency Check Output</summary>

  ```text
  [output of dependencyLockCheck]
  ```
</details>

## Does this PR require a lock file change?

Yes/No

_If yes then this will need to form part of a new major release_

## Checklist

- [ ] I have read the CONTRIBUTING guidelines
- [ ] User documentation has been updated
- [ ] Unit and/or sbt tests have been added for the changes
- [ ] I have run the following sbt commands:
  - [ ] `test`
  - [ ] `scripted`
  - [ ] `scalafmt` & `test:scalafmt`
  - [ ] `headerCheck` & `test:headerCheck`

## Description for the release notes

_If there is anything in particular you want to highlight in the release notes
then add it here._