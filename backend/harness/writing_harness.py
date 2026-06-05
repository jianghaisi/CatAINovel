"""Backward-compatible alias for the old Harness name.

Use `job_runner.WritingJobRunner` in new code. This module remains only so old
imports do not break.
"""

from job_runner import WritingJobRunner

WritingHarness = WritingJobRunner

__all__ = ["WritingHarness", "WritingJobRunner"]
