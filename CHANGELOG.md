# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [0.4-SHAPSHOT]
### Changed
- using scala CanBuildFrom for support any sequence and map

## [0.3.2] - 2017-11-16
### Added
- added new unit tests to cover more cases
- added Gson types to implicit convert and type check

### Changed
- `null` value now is recognized correctly 

## [0.3.1] - 2017-11-15
### Added
- increased unit tests covering
- added tests to check expected exceptions (incorrect format)

### Changed
- rearranged and renamed some inner classes

## [0.3.0] - 2017-11-14
### Added
- convert JsonObject to any Scala primitives and case classes, list, map
- sbt build for a project and plugins to release, format
- unit tests used as examples

### Changed

### Removed