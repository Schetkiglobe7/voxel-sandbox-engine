# Git Commit Message Convention

See how [a minor change](#examples) to your commit message style can make a difference.

<pre>
git commit -m"<b><a id="#types">&lt;type&gt;</a></b>(<b><a id="#scopes">&lt;optional scope&gt;</a></b>): <b><a id="#description">&lt;description&gt;</a></b>" \
  -m"<b><a id="#body">&lt;optional body&gt;</a></b>" \
  -m"<b><a id="#footer">&lt;optional footer&gt;</a></b>"
</pre>

> [!Note]
> This cheatsheet is opinionated, however it does not violate the specification of [conventional commits](https://www.conventionalcommits.org/)

## Commit Message Formats

### General Commit
<pre>
<b><a id="#types">&lt;type&gt;</a></b>(<b><a id="#scopes">&lt;optional scope&gt;</a></b>): <b><a id="#description">&lt;description&gt;</a></b>
<sub>empty line as separator</sub>
<b><a id="#body">&lt;optional body&gt;</a></b>
<sub>empty line as separator</sub>
<b><a id="#footer">&lt;optional footer&gt;</a></b>
</pre>

### Initial Commit
```
chore: init
```

### Merge Commit
<pre>
Merge branch '<b>&lt;branch name&gt;</b>'
</pre>
<sup>Follows default git merge message</sup>

### Revert Commit
<pre>
Revert "<b>&lt;reverted commit subject line&gt;</b>"
</pre>
<sup>Follows default git revert message</sup>


### Types
Changes relevant to the API or UI:
- `feat` Commits that add, adjust or remove a new feature to the API or UI
- `fix` Commits that fix an API or UI bug of a preceded `feat` commit
- `refactor` Commits that rewrite or restructure code without altering API or UI behavior
- `perf` Commits are special type of `refactor` commits that specifically improve performance
- `style` Commits that address code style (e.g., white-space, formatting, missing semi-colons) and do not affect application behavior
- `test` Commits that add missing tests or correct existing ones
- `docs` Commits that exclusively affect documentation
- `build` Commits that affect build-related components such as build tools, dependencies, project version, ...
- `chore` Commits that represent tasks like initial commit, modifying `.gitignore`, ...

### Scopes
The `scope` provides additional contextual information.
* The scope is an **optional** part
* Allowed scopes vary and are typically defined by the specific project
* Allowed scopes are project-specific and may evolve over time.
* **Do not** use issue identifiers as scopes

### Breaking Changes Indicator
- A commit that introduce breaking changes **must** be indicated by an `!` before the `:` in the subject line e.g. `feat(api)!: remove status endpoint`
- Breaking changes **should** be described in the [commit footer section](#footer), if the [commit description](#description) isn't sufficiently informative

### Description
The `description` contains a concise description of the change.
- The description is a **mandatory** part
- Use the imperative, present tense: "change" not "changed" nor "changes"
    - Think of `This commit will...` or `This commit should...`
- **Do not** capitalize the first letter
- **Do not** end the description with a period (`.`)
- In case of breaking changes also see [breaking changes indicator](#breaking-changes-indicator)

### Body
The `body` should include the motivation for the change and contrast this with previous behavior.
- The body is an **optional** part
- Use the imperative, present tense: "change" not "changed" nor "changes"

### Footer
The `footer` should contain issue references and informations about **Breaking Changes**
- The footer is an **optional** part, except if the commit introduce breaking changes
- *Optionally* reference issue identifiers (e.g., `Closes #123`, `Fixes JIRA-456`)
- **Breaking Changes** **must** start with the word `BREAKING CHANGE:`
    - For a single line description just add a space after `BREAKING CHANGE:`
    - For a multi line description add two new lines after `BREAKING CHANGE:`

### Versioning
- **If** your next release contains commit with...
    - **Breaking Changes** incremented the **major version**
    - **API relevant changes** (`feat` or `fix`) incremented the **minor version**
- **Else** increment the **patch version**


### Examples
- ```
  feat: add basic voxel world bootstrap
  ```
- ```
  feat(core): introduce engine lifecycle initialization
  ```
- ```
  feat(world)!: remove implicit chunk coordinate derivation

  BREAKING CHANGE: chunk identifiers must now be provided explicitly when loading world data.
  ```
- ```
  fix(world): prevent loading empty chunks during world generation
  ```
- ```
  fix(data): correct checksum calculation for persisted chunk data
  ```
- ```
  fix: add missing configuration parameter to engine startup

  The issue occurred when the default configuration file was not provided.
  ```
- ```
  perf(render): reduce memory usage during chunk mesh generation
  ```
- ```
  build: update build dependencies
  ```
- ```
  build(release): bump version to 0.1.0
  ```
- ```
  refactor(core): simplify chunk lookup logic
  ```
- ```
  style: remove extra blank line in configuration loader
  ```
  
-----
## References
- https://www.conventionalcommits.org/
- https://github.com/angular/angular/blob/master/CONTRIBUTING.md
- http://karma-runner.github.io/1.0/dev/git-commit-msg.html
