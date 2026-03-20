#!/usr/bin/env bash
#
# (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
#
# This file is part of the Kitodo project.
#
# It is licensed under GNU General Public License version 3 or later.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.
#

#
# Note: Ensure that Tomcat has permission to execute the given commands.
#

export PATH="/usr/bin:/bin:${PATH}"

User="$1"
Home="$2"

mkdir "$Home"
chmod g+w "$Home"
chown $User "$Home"
chgrp tomcat "$Home"
