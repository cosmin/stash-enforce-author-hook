# Enforce Author Hook for Stash

Unlike centralized source control systems, in Git the commit author
information cannot be trusted by default. The local user is in
complete control of the information recorded as the 'author' of each
commit on their machine.

Stash by default will allow any authenticated user to push any
commits, whether or not the user performing the push is also the
author of the commits being pushed. For a distributed version control
feature, this is a necessary feature. However, in an enterprise
setting this is not always desireable from an auditing point of view.

The Enforce Author Hook for Stash can help you satisfy auditing
requirements by only allowing users to push commits that they have
authored. It does this by matching the email address and display name
on the commit against the information stored in Stash for the user
performing the push.


## Installation

This add-on is available for free on the [Atlassian Marketplace]("https://marketplace.atlassian.com/plugins/com.risingoak.stash.plugins.stash-enforce-author-hook").

## License

Copyright 2013, Rising Oak LLC.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
    
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

## Support

The free version of this add-on is not supported. Commercial support
is available. Contact sales@risingoak.com for details.
